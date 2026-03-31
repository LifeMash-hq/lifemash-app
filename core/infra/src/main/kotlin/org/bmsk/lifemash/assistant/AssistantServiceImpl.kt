package org.bmsk.lifemash.assistant

import kotlin.time.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlinx.serialization.json.*
import org.bmsk.lifemash.model.assistant.ChatRequest
import org.bmsk.lifemash.model.assistant.ConversationDetailDto
import org.bmsk.lifemash.model.assistant.ConversationDto
import org.bmsk.lifemash.model.assistant.InstalledBlockContext
import org.bmsk.lifemash.model.assistant.MessageDto
import org.bmsk.lifemash.model.assistant.SseEvent
import org.bmsk.lifemash.model.assistant.UsageInfo
import org.bmsk.lifemash.model.assistant.UsageResponse
import org.bmsk.lifemash.marketplace.MarketplaceRepository
import org.bmsk.lifemash.plugins.BadRequestException
import kotlin.uuid.Uuid

/**
 * AI 어시스턴트 핵심 서비스 — 채팅 처리의 전체 흐름을 관리.
 *
 * 채팅 처리 흐름:
 * 1. API 키 확인 (사용자 키 또는 서버 키)
 * 2. 사용자 키가 없으면 일일 사용 한도 확인
 * 3. 대화 생성/이어가기
 * 4. Tool Use 루프: AI가 도구(일정 조회 등)를 호출해야 하면 실행 후 결과를 AI에 전달
 * 5. 최종 응답을 스트리밍으로 클라이언트에 전달
 * 6. 메시지와 사용량을 DB에 저장
 */
class AssistantServiceImpl(
    private val claudeApiClient: ClaudeApiClient,
    private val assistantRepository: AssistantRepository,
    private val usageRepository: AssistantUsageRepository,
    private val apiKeyRepository: UserApiKeyRepository,
    private val toolRegistry: ToolRegistry,
    private val marketplaceRepository: MarketplaceRepository,
    private val externalToolExecutor: ExternalToolExecutor,
    private val serverApiKey: String? = org.bmsk.lifemash.config.EnvConfig.get("CLAUDE_API_KEY"),
) : AssistantService {

    private val json = Json { ignoreUnknownKeys = true }

    /**
     * 채팅 메인 함수 — SSE 이벤트를 emitEvent 콜백으로 클라이언트에 실시간 전달.
     * emitEvent가 호출될 때마다 클라이언트에 JSON이 한 줄씩 전송됨.
     */
    override suspend fun chat(
        userId: String,
        request: ChatRequest,
        emitEvent: suspend (SseEvent) -> Unit,
    ) {
        val userUuid = Uuid.parse(userId)
        val message = request.message.trim()

        if (message.isEmpty()) throw BadRequestException("메시지를 입력해 주세요.")
        if (message.length > MAX_MESSAGE_LENGTH) throw BadRequestException("메시지는 ${MAX_MESSAGE_LENGTH}자 이내로 입력해 주세요.")

        val userApiKey = apiKeyRepository.getDecryptedApiKey(userUuid)
        val apiKey = userApiKey ?: serverApiKey
            ?: throw BadRequestException("서버 API 키가 설정되지 않았습니다.")

        if (userApiKey == null) {
            val today = Clock.System.todayIn(TimeZone.UTC)
            val requestCount = usageRepository.getRequestCount(userUuid, today)
            if (requestCount >= DAILY_REQUEST_LIMIT) {
                throw BadRequestException("일일 사용 한도(${DAILY_REQUEST_LIMIT}회)를 초과했습니다. 직접 API 키를 등록하면 무제한으로 사용할 수 있습니다.")
            }
        }

        val reqConversationId = request.conversationId
        val conversationId = if (reqConversationId != null) {
            val convUuid = Uuid.parse(reqConversationId)
            if (!assistantRepository.isConversationOwner(convUuid, userUuid)) {
                throw BadRequestException("대화를 찾을 수 없습니다.")
            }
            convUuid
        } else {
            val title = message.take(50)
            assistantRepository.createConversation(userUuid, title).let { Uuid.parse(it.id) }
        }

        val history = assistantRepository.getRecentMessages(conversationId)
        val claudeMessages = buildClaudeMessages(history, message)

        val externalToolDefs = getExternalToolDefinitions(request.installedBlocks)
        val systemPrompt = buildSystemPrompt(request.installedBlocks)

        var totalInputTokens = 0
        var totalOutputTokens = 0

        try {
            val toolUseResult = executeToolUseLoop(
                apiKey, claudeMessages, userUuid, request.installedBlocks, externalToolDefs, systemPrompt, emitEvent,
            )
            totalInputTokens += toolUseResult.inputTokens
            totalOutputTokens += toolUseResult.outputTokens

            val responseText: String
            if (toolUseResult.finalText != null) {
                responseText = toolUseResult.finalText
                emitEvent(SseEvent(type = "token", content = responseText))
            } else {
                val streamResult = claudeApiClient.sendMessageStreaming(
                    apiKey = apiKey,
                    messages = toolUseResult.finalMessages,
                    tools = toolRegistry.getAllToolDefinitions() + externalToolDefs,
                    systemPrompt = systemPrompt,
                ) { delta ->
                    emitEvent(SseEvent(type = "token", content = delta))
                }
                totalInputTokens += streamResult.inputTokens
                totalOutputTokens += streamResult.outputTokens
                responseText = streamResult.fullText
            }

            assistantRepository.addMessage(conversationId, "user", message)
            assistantRepository.addMessage(
                conversationId,
                "assistant",
                responseText,
                toolCallsJson = toolUseResult.toolCallsJson,
            )
            assistantRepository.updateConversationTimestamp(conversationId)

            val today = Clock.System.todayIn(TimeZone.UTC)
            usageRepository.incrementUsage(userUuid, today, totalInputTokens, totalOutputTokens)

            emitEvent(
                SseEvent(
                    type = "done",
                    conversationId = conversationId.toString(),
                    usage = UsageInfo(totalInputTokens, totalOutputTokens),
                )
            )
        } catch (e: RateLimitException) {
            emitEvent(SseEvent(type = "error", content = "잠시 후 다시 시도해 주세요."))
        } catch (e: InvalidApiKeyException) {
            emitEvent(SseEvent(type = "error", content = "API 키가 유효하지 않습니다."))
        } catch (e: ClaudeApiException) {
            emitEvent(SseEvent(type = "error", content = "AI 응답 중 오류가 발생했습니다."))
        }
    }

    /**
     * Tool Use 루프: AI가 도구 호출을 요청하면 실행 → 결과를 AI에 전달 → 반복.
     * 최대 MAX_TOOL_CALLS(5회)까지 반복.
     *
     * AI의 stop_reason이 "tool_use"이면 아직 도구 결과가 필요하다는 뜻이고,
     * "end_turn"이면 더 이상 도구가 필요 없이 답변 준비가 된 것.
     */
    private suspend fun executeToolUseLoop(
        apiKey: String,
        initialMessages: MutableList<ClaudeMessage>,
        userId: Uuid,
        installedBlocks: List<InstalledBlockContext>,
        externalToolDefs: List<JsonObject>,
        systemPrompt: String,
        emitEvent: suspend (SseEvent) -> Unit,
    ): ToolUseLoopResult {
        val messages = initialMessages
        var totalInputTokens = 0
        var totalOutputTokens = 0
        val toolCallsRecord = mutableListOf<JsonObject>()

        repeat(MAX_TOOL_CALLS) {
            val response = claudeApiClient.sendMessage(
                apiKey = apiKey,
                messages = messages,
                tools = toolRegistry.getAllToolDefinitions() + externalToolDefs,
                systemPrompt = systemPrompt,
            )

            totalInputTokens += response.usage?.input_tokens ?: 0
            totalOutputTokens += response.usage?.output_tokens ?: 0

            val toolUseBlocks = response.content.filter { it.type == "tool_use" }
            if (toolUseBlocks.isEmpty() || response.stop_reason != "tool_use") {
                val finalText = response.content
                    .filter { it.type == "text" }
                    .mapNotNull { it.text }
                    .joinToString("")
                return ToolUseLoopResult(
                    finalMessages = messages,
                    finalText = finalText.ifEmpty { null },
                    inputTokens = totalInputTokens,
                    outputTokens = totalOutputTokens,
                    toolCallsJson = if (toolCallsRecord.isNotEmpty()) {
                        buildJsonArray { toolCallsRecord.forEach { add(it) } }.toString()
                    } else null,
                )
            }

            // 도구 호출 전 Claude가 생성한 안내 텍스트를 사용자에게 전달
            response.content.filter { it.type == "text" }.forEach { block ->
                block.text?.let { text ->
                    if (text.isNotBlank()) emitEvent(SseEvent(type = "token", content = text))
                }
            }

            val assistantContent = buildJsonArray {
                response.content.forEach { block ->
                    when (block.type) {
                        "text" -> add(buildJsonObject {
                            put("type", "text")
                            put("text", block.text)
                        })
                        "tool_use" -> add(buildJsonObject {
                            put("type", "tool_use")
                            put("id", block.id)
                            put("name", block.name)
                            block.input?.let { put("input", it as JsonElement) }
                        })
                    }
                }
            }
            messages.add(ClaudeMessage(role = "assistant", content = assistantContent))

            val toolResults = buildJsonArray {
                toolUseBlocks.forEach { block ->
                    val toolName = block.name ?: return@forEach
                    val toolInput = block.input ?: JsonObject(emptyMap())
                    val toolId = block.id ?: return@forEach

                    emitEvent(SseEvent(type = "tool_start", tool = toolName))
                    val result = if (toolRegistry.isKnownTool(toolName)) {
                        toolRegistry.executeTool(userId, toolName, toolInput)
                    } else {
                        val executionUrl = findExecutionUrl(installedBlocks, toolName)
                        if (executionUrl != null) {
                            val resultStr = externalToolExecutor.execute(executionUrl, toolName, toolInput)
                            ToolResult(resultStr)
                        } else {
                            ToolResult("""{"error": "Unknown tool: $toolName"}""", isError = true)
                        }
                    }
                    emitEvent(SseEvent(type = "tool_end", tool = toolName))

                    toolCallsRecord.add(buildJsonObject {
                        put("tool", toolName)
                        put("input", toolInput)
                        put("output", result.content)
                    })

                    add(buildJsonObject {
                        put("type", "tool_result")
                        put("tool_use_id", toolId)
                        put("content", result.content)
                        if (result.isError) put("is_error", true)
                    })
                }
            }
            messages.add(ClaudeMessage(role = "user", content = toolResults))
        }

        return ToolUseLoopResult(
            finalMessages = messages,
            inputTokens = totalInputTokens,
            outputTokens = totalOutputTokens,
            toolCallsJson = if (toolCallsRecord.isNotEmpty()) {
                buildJsonArray { toolCallsRecord.forEach { add(it) } }.toString()
            } else null,
        )
    }

    /** 설치된 블록들의 tool_definitions에서 외부 도구 정의를 추출 (executionUrl 제외하고 Claude에 전달). */
    private fun getExternalToolDefinitions(installedBlocks: List<InstalledBlockContext>): List<JsonObject> {
        return installedBlocks.flatMap { block ->
            val dto = marketplaceRepository.findById(block.id) ?: return@flatMap emptyList()
            val definitions = dto.toolDefinitions ?: return@flatMap emptyList()
            json.decodeFromString<JsonArray>(definitions)
                .mapNotNull { it as? JsonObject }
                .map { tool ->
                    buildJsonObject {
                        put("name", tool["name"]!!)
                        put("description", tool["description"]!!)
                        put("input_schema", tool["input_schema"]!!)
                    }
                }
        }
    }

    /** 설치된 블록들의 tool_definitions에서 특정 도구의 executionUrl을 찾아 반환. */
    private fun findExecutionUrl(installedBlocks: List<InstalledBlockContext>, toolName: String): String? {
        for (block in installedBlocks) {
            val dto = marketplaceRepository.findById(block.id) ?: continue
            val definitions = dto.toolDefinitions ?: continue
            val tools = json.decodeFromString<JsonArray>(definitions)
            for (element in tools) {
                val tool = element as? JsonObject ?: continue
                if (tool["name"]?.jsonPrimitive?.content == toolName) {
                    return tool["executionUrl"]?.jsonPrimitive?.content
                }
            }
        }
        return null
    }

    /** 기존 SYSTEM_PROMPT에 설치된 앱 컨텍스트를 추가하여 시스템 프롬프트 생성. */
    private fun buildSystemPrompt(installedBlocks: List<InstalledBlockContext>): String {
        if (installedBlocks.isEmpty()) return SYSTEM_PROMPT

        val appLines = installedBlocks.joinToString("\n") { block ->
            val dto = marketplaceRepository.findById(block.id)
            val toolNames = dto?.toolDefinitions?.let { defs ->
                json.decodeFromString<JsonArray>(defs)
                    .mapNotNull { (it as? JsonObject)?.get("name")?.jsonPrimitive?.content }
                    .joinToString(", ")
            } ?: ""
            "- ${dto?.name ?: block.id}${if (toolNames.isNotEmpty()) " (도구: $toolNames)" else ""}"
        }
        return SYSTEM_PROMPT + "\n\n사용자가 설치한 앱:\n$appLines\n설치된 앱의 도구를 사용자 요청에 맞게 활용하세요."
    }

    /** 기존 대화 이력 + 새 메시지를 Claude API 형식의 메시지 배열로 변환 */
    private fun buildClaudeMessages(
        history: List<MessageDto>,
        newMessage: String,
    ): MutableList<ClaudeMessage> {
        val messages = mutableListOf<ClaudeMessage>()
        history.forEach { msg ->
            messages.add(
                ClaudeMessage(
                    role = msg.role,
                    content = JsonPrimitive(msg.content),
                )
            )
        }
        messages.add(ClaudeMessage(role = "user", content = JsonPrimitive(newMessage)))
        return messages
    }

    override fun getConversations(userId: String, limit: Int, offset: Long): List<ConversationDto> =
        assistantRepository.getConversations(Uuid.parse(userId), limit, offset)

    override fun getConversationDetail(userId: String, conversationId: String): ConversationDetailDto {
        val convUuid = Uuid.parse(conversationId)
        val userUuid = Uuid.parse(userId)
        if (!assistantRepository.isConversationOwner(convUuid, userUuid)) {
            throw BadRequestException("대화를 찾을 수 없습니다.")
        }
        return assistantRepository.getConversationDetail(convUuid)
            ?: throw BadRequestException("대화를 찾을 수 없습니다.")
    }

    override fun deleteConversation(userId: String, conversationId: String) {
        val convUuid = Uuid.parse(conversationId)
        val userUuid = Uuid.parse(userId)
        if (!assistantRepository.deleteConversation(convUuid, userUuid)) {
            throw BadRequestException("대화를 찾을 수 없습니다.")
        }
    }

    override fun getUsage(userId: String, date: LocalDate?): UsageResponse {
        val targetDate = date ?: Clock.System.todayIn(TimeZone.UTC)
        return usageRepository.getUsage(Uuid.parse(userId), targetDate)
    }

    companion object {
        private const val MAX_TOOL_CALLS = 5
        private const val MAX_MESSAGE_LENGTH = 2000
        private const val DAILY_REQUEST_LIMIT = 20

        private const val SYSTEM_PROMPT = """당신은 LifeMash 플랫폼의 AI 어시스턴트입니다.
사용자의 캘린더 일정, 그룹 정보를 조회하고 관리할 수 있습니다.
친절하고 간결하게 한국어로 응답하세요.

[내장 도구]
- get_today_events: 오늘 일정 조회
- get_month_events: 월간 일정 조회
- get_my_groups: 사용자 그룹 목록 조회
- create_event: 캘린더에 새 일정 생성 (groupId 필요 → get_my_groups로 먼저 조회)

[설치된 앱 도구]
사용자가 설치한 앱의 도구도 사용할 수 있습니다.
뉴스 검색, 일정 반영 등 복합 요청 시 여러 도구를 순서대로 조합하세요.
예: 뉴스 검색 → 결과에서 일정 추출 → create_event로 캘린더에 반영

도구 실행 결과가 비어있으면 솔직하게 알리고, 다른 키워드를 제안하세요.

[응답 규칙]
도구를 사용할 때는 반드시 다음 순서로 응답하세요:
1. 먼저 어떤 서비스를 활용해서 무엇을 할지 짧게 안내하세요.
   예: "LifeMash 뉴스에서 삼성전자 관련 기사를 검색하겠습니다."
2. 도구를 실행하세요.
3. 완료 후 결과를 요약하여 안내하세요.
   예: "3건의 기사를 찾아 캘린더에 반영했습니다.""""
    }
}

private data class ToolUseLoopResult(
    val finalMessages: MutableList<ClaudeMessage>,
    val finalText: String? = null,
    val inputTokens: Int,
    val outputTokens: Int,
    val toolCallsJson: String?,
)
