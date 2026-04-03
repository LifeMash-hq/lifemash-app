package org.bmsk.lifemash.assistant

import kotlin.time.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.uuid.Uuid
import org.bmsk.lifemash.model.assistant.ChatRequest
import org.bmsk.lifemash.model.assistant.ConversationDetailDto
import org.bmsk.lifemash.model.assistant.ConversationDto
import org.bmsk.lifemash.model.assistant.SseEvent
import org.bmsk.lifemash.model.assistant.UsageInfo
import org.bmsk.lifemash.model.assistant.UsageResponse
import org.bmsk.lifemash.plugins.ForbiddenException
import org.bmsk.lifemash.plugins.NotFoundException
import org.bmsk.lifemash.validation.AssistantLimits
import org.bmsk.lifemash.validation.ChatMessageContent

class AssistantServiceImpl(
    private val assistantRepository: AssistantRepository,
    private val usageRepository: AssistantUsageRepository,
    private val apiKeyRepository: UserApiKeyRepository,
    private val claudeApiClient: ClaudeApiClient,
    private val toolRegistry: ToolRegistry,
    private val externalToolExecutor: ExternalToolExecutor,
) : AssistantService {

    override suspend fun chat(
        userId: String,
        request: ChatRequest,
        emitEvent: suspend (SseEvent) -> Unit,
    ) {
        val userUuid = Uuid.parse(userId)
        val today = Clock.System.now().toLocalDateTime(TimeZone.UTC).date

        // Step 1: 사전 검증
        try {
            ChatMessageContent.of(request.message)
        } catch (e: IllegalArgumentException) {
            emitEvent(SseEvent(type = "error", content = e.message))
            return
        }

        val apiKey = apiKeyRepository.getDecryptedApiKey(userUuid)
        val hasApiKey = apiKey != null
        val requestCount = usageRepository.getRequestCount(userUuid, today)

        if (!AssistantLimits.canSendMessage(requestCount, hasApiKey)) {
            emitEvent(SseEvent(type = "error", content = "일일 요청 한도(${AssistantLimits.DAILY_REQUEST_LIMIT}회)를 초과했습니다."))
            return
        }

        if (apiKey == null) {
            emitEvent(SseEvent(type = "error", content = "API 키가 설정되지 않았습니다. 설정에서 Claude API 키를 등록해주세요."))
            return
        }

        // Step 2: 대화 생성 또는 조회
        val requestConversationId = request.conversationId
        val conversationUuid: Uuid = if (requestConversationId != null) {
            val convId = Uuid.parse(requestConversationId)
            if (!assistantRepository.isConversationOwner(convId, userUuid)) {
                emitEvent(SseEvent(type = "error", content = "대화에 접근할 수 없습니다."))
                return
            }
            convId
        } else {
            val title = request.message.take(50)
            val conv = assistantRepository.createConversation(userUuid, title)
            emitEvent(SseEvent(type = "conversation_created", conversationId = conv.id))
            Uuid.parse(conv.id)
        }

        // Step 3~6: 메시지 처리 (에러 핸들링 포함)
        try {
            assistantRepository.addMessage(conversationUuid, "user", request.message)
            val recentMessages = assistantRepository.getRecentMessages(conversationUuid)

            val mutableMessages = recentMessages.map { msg ->
                ClaudeMessage(role = msg.role, content = JsonPrimitive(msg.content))
            }.toMutableList()

            val tools = toolRegistry.getAllToolDefinitions()
            var totalInputTokens = 0
            var totalOutputTokens = 0
            var iterations = 0

            // Step 4: Tool-use 루프
            while (iterations < MAX_TOOL_ITERATIONS) {
                iterations++
                val response = claudeApiClient.sendMessage(apiKey, mutableMessages, tools, SYSTEM_PROMPT)
                totalInputTokens += response.usage?.input_tokens ?: 0
                totalOutputTokens += response.usage?.output_tokens ?: 0

                if (response.stop_reason != "tool_use") {
                    val textContent = response.content
                        .filter { it.type == "text" }
                        .joinToString("") { it.text.orEmpty() }

                    assistantRepository.addMessage(conversationUuid, "assistant", textContent)
                    emitEvent(SseEvent(type = "delta", content = textContent))
                    break
                }

                // assistant 메시지를 JsonArray 형태로 추가
                val assistantContent = buildJsonArray {
                    response.content.forEach { block ->
                        add(buildJsonObject {
                            put("type", block.type)
                            when (block.type) {
                                "text" -> if (block.text != null) put("text", block.text)
                                "tool_use" -> {
                                    put("id", block.id!!)
                                    put("name", block.name!!)
                                    put("input", block.input!!)
                                }
                            }
                        })
                    }
                }
                mutableMessages.add(ClaudeMessage(role = "assistant", content = assistantContent))

                // 각 tool_use 블록 실행 후 tool_result 구성
                val toolUseBlocks = response.content.filter { it.type == "tool_use" }
                val toolResultContent = buildJsonArray {
                    for (block in toolUseBlocks) {
                        val toolName = block.name!!
                        val toolInput = block.input!!
                        val toolUseId = block.id!!

                        emitEvent(SseEvent(type = "tool_use", tool = toolName))

                        val resultContent: String = if (toolRegistry.isKnownTool(toolName)) {
                            toolRegistry.executeTool(userUuid, toolName, toolInput).content
                        } else {
                            val matchedBlock = request.installedBlocks.find { it.id == toolName }
                            if (matchedBlock != null) {
                                externalToolExecutor.execute(matchedBlock.url, toolName, toolInput)
                            } else {
                                """{"error": "알 수 없는 도구: $toolName"}"""
                            }
                        }

                        add(buildJsonObject {
                            put("type", "tool_result")
                            put("tool_use_id", toolUseId)
                            put("content", resultContent)
                        })
                    }
                }
                mutableMessages.add(ClaudeMessage(role = "user", content = toolResultContent))
            }

            // Step 5: 사용량 기록 + 완료 이벤트
            usageRepository.incrementUsage(userUuid, today, totalInputTokens, totalOutputTokens)
            assistantRepository.updateConversationTimestamp(conversationUuid)
            emitEvent(
                SseEvent(
                    type = "done",
                    conversationId = conversationUuid.toString(),
                    usage = UsageInfo(inputTokens = totalInputTokens, outputTokens = totalOutputTokens),
                )
            )
        } catch (e: RateLimitException) {
            emitEvent(SseEvent(type = "error", content = "API 요청 한도를 초과했습니다. 잠시 후 다시 시도해주세요."))
        } catch (e: InvalidApiKeyException) {
            emitEvent(SseEvent(type = "error", content = "API 키가 유효하지 않습니다. 설정에서 키를 확인해주세요."))
        } catch (e: ClaudeApiException) {
            emitEvent(SseEvent(type = "error", content = "AI 서비스 오류: ${e.message}"))
        } catch (e: Exception) {
            emitEvent(SseEvent(type = "error", content = "오류가 발생했습니다. 잠시 후 다시 시도해주세요."))
        }
    }

    override fun getConversations(userId: String, limit: Int, offset: Long): List<ConversationDto> {
        return assistantRepository.getConversations(Uuid.parse(userId), limit, offset)
    }

    override fun getConversationDetail(userId: String, conversationId: String): ConversationDetailDto {
        val userUuid = Uuid.parse(userId)
        val convUuid = Uuid.parse(conversationId)
        if (!assistantRepository.isConversationOwner(convUuid, userUuid)) {
            throw ForbiddenException("대화에 접근할 수 없습니다.")
        }
        return assistantRepository.getConversationDetail(convUuid)
            ?: throw NotFoundException("대화를 찾을 수 없습니다.")
    }

    override fun deleteConversation(userId: String, conversationId: String) {
        val deleted = assistantRepository.deleteConversation(
            conversationId = Uuid.parse(conversationId),
            userId = Uuid.parse(userId),
        )
        if (!deleted) throw NotFoundException("대화를 찾을 수 없습니다.")
    }

    override fun getUsage(userId: String, date: LocalDate?): UsageResponse {
        val effectiveDate = date ?: Clock.System.now().toLocalDateTime(TimeZone.UTC).date
        return usageRepository.getUsage(Uuid.parse(userId), effectiveDate)
    }

    companion object {
        private const val MAX_TOOL_ITERATIONS = 10
        private const val SYSTEM_PROMPT = """당신은 LifeMash 앱의 AI 어시스턴트입니다.
사용자의 일정 관리와 소셜 캘린더 활동을 도와줍니다.
도구를 사용하여 캘린더 일정을 조회하거나 생성할 수 있습니다.
한국어로 자연스럽게 대화합니다."""
    }
}
