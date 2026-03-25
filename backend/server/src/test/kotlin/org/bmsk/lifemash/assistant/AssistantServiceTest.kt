package org.bmsk.lifemash.assistant

import kotlinx.coroutines.test.runTest
import kotlin.time.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import io.ktor.client.*
import org.bmsk.lifemash.assistant.tools.CalendarTool
import org.bmsk.lifemash.fake.*
import org.bmsk.lifemash.model.assistant.ChatRequest
import org.bmsk.lifemash.model.assistant.SseEvent
import org.bmsk.lifemash.model.assistant.UsageResponse
import org.bmsk.lifemash.plugins.BadRequestException
import java.util.*
import kotlin.test.*

class AssistantServiceTest {

    private lateinit var claudeClient: FakeClaudeApiClient
    private lateinit var assistantRepo: FakeAssistantRepository
    private lateinit var usageRepo: FakeAssistantUsageRepository
    private lateinit var apiKeyRepo: FakeUserApiKeyRepository
    private lateinit var service: AssistantService

    private val userId = UUID.randomUUID()
    private val fakeMarketplaceRepo = FakeMarketplaceRepository()
    private val fakeExternalToolExecutor = ExternalToolExecutor(HttpClient())

    @BeforeTest
    fun setUp() {
        claudeClient = FakeClaudeApiClient()
        assistantRepo = FakeAssistantRepository()
        usageRepo = FakeAssistantUsageRepository()
        apiKeyRepo = FakeUserApiKeyRepository()
        val eventRepo = FakeEventRepository()
        val groupRepo = FakeGroupRepository()
        val calendarTool = CalendarTool(eventRepo, groupRepo, fakeEventService())
        val toolRegistry = ToolRegistry(calendarTool)
        service = AssistantServiceImpl(
            claudeApiClient = claudeClient,
            assistantRepository = assistantRepo,
            usageRepository = usageRepo,
            apiKeyRepository = apiKeyRepo,
            toolRegistry = toolRegistry,
            marketplaceRepository = fakeMarketplaceRepo,
            externalToolExecutor = fakeExternalToolExecutor,
            serverApiKey = "test-server-key",
        )
    }

    private suspend fun collectEvents(block: suspend ((SseEvent) -> Unit) -> Unit): List<SseEvent> {
        val events = mutableListOf<SseEvent>()
        block { events.add(it) }
        return events
    }

    @Test
    fun `빈 메시지는 IllegalArgumentException을 발생시킨다`() = runTest {
        // When & Then
        assertFailsWith<IllegalArgumentException> {
            service.chat(userId.toString(), ChatRequest(message = "   ")) {}
        }
    }

    @Test
    fun `2000자 초과 메시지는 IllegalArgumentException을 발생시킨다`() = runTest {
        // When & Then
        assertFailsWith<IllegalArgumentException> {
            service.chat(userId.toString(), ChatRequest(message = "a".repeat(2001))) {}
        }
    }

    @Test
    fun `사용자 API 키가 있으면 해당 키로 호출한다`() = runTest {
        // Given
        apiKeyRepo.saveApiKey(userId, "user-api-key")

        // When
        service.chat(userId.toString(), ChatRequest(message = "안녕")) {}

        // Then
        assertEquals("user-api-key", claudeClient.lastApiKey)
    }

    @Test
    fun `사용자 키도 서버 키도 없으면 BadRequestException이 발생한다`() = runTest {
        // Given
        val noKeyService = AssistantServiceImpl(
            claudeApiClient = claudeClient,
            assistantRepository = assistantRepo,
            usageRepository = usageRepo,
            apiKeyRepository = apiKeyRepo,
            toolRegistry = ToolRegistry(CalendarTool(FakeEventRepository(), FakeGroupRepository(), fakeEventService())),
            marketplaceRepository = fakeMarketplaceRepo,
            externalToolExecutor = fakeExternalToolExecutor,
            serverApiKey = null,
        )

        // When & Then
        assertFailsWith<BadRequestException> {
            noKeyService.chat(userId.toString(), ChatRequest(message = "안녕")) {}
        }
    }

    @Test
    fun `일일 사용 한도 초과 시 BadRequestException이 발생한다`() = runTest {
        // Given
        val today = Clock.System.todayIn(TimeZone.UTC)
        usageRepo.setRequestCount(userId, today, 20)

        // When & Then
        assertFailsWith<BadRequestException> {
            service.chat(userId.toString(), ChatRequest(message = "안녕")) {}
        }
    }

    @Test
    fun `사용자 API 키가 있으면 사용량 제한을 확인하지 않는다`() = runTest {
        // Given
        apiKeyRepo.saveApiKey(userId, "user-key")
        val today = Clock.System.todayIn(TimeZone.UTC)
        usageRepo.setRequestCount(userId, today, 100) // 한도 초과

        // When — 예외 없이 정상 실행
        val events = collectEvents { emit ->
            service.chat(userId.toString(), ChatRequest(message = "안녕"), emit)
        }

        // Then
        assertTrue(events.any { it.type == "done" })
    }

    @Test
    fun `새 대화가 생성되고 conversationId가 반환된다`() = runTest {
        // When
        val events = collectEvents { emit ->
            service.chat(userId.toString(), ChatRequest(message = "안녕하세요"), emit)
        }

        // Then
        val doneEvent = events.find { it.type == "done" }
        assertNotNull(doneEvent)
        assertNotNull(doneEvent.conversationId)
    }

    @Test
    fun `다른 사용자의 대화에 접근 시 BadRequestException이 발생한다`() = runTest {
        // Given — userId로 대화 생성
        val events = collectEvents { emit ->
            service.chat(userId.toString(), ChatRequest(message = "안녕"), emit)
        }
        val convId = events.find { it.type == "done" }!!.conversationId!!

        // When & Then — 다른 사용자가 접근
        val otherId = UUID.randomUUID()
        assertFailsWith<BadRequestException> {
            service.chat(otherId.toString(), ChatRequest(message = "안녕", conversationId = convId)) {}
        }
    }

    @Test
    fun `채팅 완료 후 메시지가 DB에 저장된다`() = runTest {
        // When
        val events = collectEvents { emit ->
            service.chat(userId.toString(), ChatRequest(message = "일정 알려줘"), emit)
        }
        val convId = events.find { it.type == "done" }!!.conversationId!!

        // Then
        val messages = assistantRepo.getMessagesFor(convId)
        assertEquals(2, messages.size) // user + assistant
        assertEquals("user", messages[0].role)
        assertEquals("assistant", messages[1].role)
    }

    @Test
    fun `채팅 완료 후 사용량이 증가한다`() = runTest {
        // When
        service.chat(userId.toString(), ChatRequest(message = "안녕")) {}

        // Then
        val today = Clock.System.todayIn(TimeZone.UTC)
        assertEquals(1, usageRepo.getRequestCount(userId, today))
    }

    @Test
    fun `대화를 삭제한다`() = runTest {
        // Given
        val events = collectEvents { emit ->
            service.chat(userId.toString(), ChatRequest(message = "안녕"), emit)
        }
        val convId = events.find { it.type == "done" }!!.conversationId!!

        // When
        service.deleteConversation(userId.toString(), convId)

        // Then
        assertFailsWith<BadRequestException> {
            service.getConversationDetail(userId.toString(), convId)
        }
    }

    @Test
    fun `다른 사용자의 대화 상세 조회 시 BadRequestException이 발생한다`() = runTest {
        // Given
        val events = collectEvents { emit ->
            service.chat(userId.toString(), ChatRequest(message = "안녕"), emit)
        }
        val convId = events.find { it.type == "done" }!!.conversationId!!

        // When & Then
        assertFailsWith<BadRequestException> {
            service.getConversationDetail(UUID.randomUUID().toString(), convId)
        }
    }

    // ── Tool Use 루프 테스트 ──

    @Test
    fun `도구 호출 없으면 루프가 즉시 종료된다`() = runTest {
        // Given — 기본 end_turn 응답 (도구 호출 없음)

        // When
        val events = collectEvents { emit ->
            service.chat(userId.toString(), ChatRequest(message = "안녕하세요"), emit)
        }

        // Then
        assertEquals(1, claudeClient.sendMessageCallCount)
        assertFalse(events.any { it.type == "tool_start" })
    }

    @Test
    fun `단일 도구 호출 시 tool_start와 tool_end 이벤트가 발생한다`() = runTest {
        // Given — 1번째: tool_use 응답, 2번째: end_turn 응답
        claudeClient.enqueueSendMessageResponse(
            ClaudeResponse(
                id = "msg_1",
                content = listOf(
                    ContentBlock(
                        type = "tool_use",
                        id = "call_1",
                        name = "get_my_groups",
                        input = buildJsonObject { },
                    ),
                ),
                stop_reason = "tool_use",
                usage = ClaudeUsage(input_tokens = 15, output_tokens = 10),
            )
        )
        claudeClient.enqueueSendMessageResponse(
            ClaudeResponse(
                id = "msg_2",
                content = listOf(ContentBlock(type = "text", text = "그룹 정보입니다.")),
                stop_reason = "end_turn",
                usage = ClaudeUsage(input_tokens = 20, output_tokens = 15),
            )
        )

        // When
        val events = collectEvents { emit ->
            service.chat(userId.toString(), ChatRequest(message = "내 그룹 알려줘"), emit)
        }

        // Then
        assertEquals(2, claudeClient.sendMessageCallCount)
        assertTrue(events.any { it.type == "tool_start" && it.tool == "get_my_groups" })
        assertTrue(events.any { it.type == "tool_end" && it.tool == "get_my_groups" })
        assertTrue(events.any { it.type == "done" })
    }

    @Test
    fun `최대 도구 호출 횟수에 도달하면 루프가 종료된다`() = runTest {
        // Given — 5개 tool_use 응답 enqueue (MAX_TOOL_CALLS == 5)
        repeat(5) { i ->
            claudeClient.enqueueSendMessageResponse(
                ClaudeResponse(
                    id = "msg_$i",
                    content = listOf(
                        ContentBlock(
                            type = "tool_use",
                            id = "call_$i",
                            name = "get_my_groups",
                            input = buildJsonObject { },
                        ),
                    ),
                    stop_reason = "tool_use",
                    usage = ClaudeUsage(input_tokens = 10, output_tokens = 5),
                )
            )
        }

        // When
        val events = collectEvents { emit ->
            service.chat(userId.toString(), ChatRequest(message = "그룹 알려줘"), emit)
        }

        // Then — sendMessage 5회 (tool use loop) 호출
        assertEquals(5, claudeClient.sendMessageCallCount)
        assertTrue(events.any { it.type == "done" })
    }

    @Test
    fun `알 수 없는 도구 호출 시에도 채팅이 정상 완료된다`() = runTest {
        // Given — unknown tool 호출 후 end_turn
        claudeClient.enqueueSendMessageResponse(
            ClaudeResponse(
                id = "msg_1",
                content = listOf(
                    ContentBlock(
                        type = "tool_use",
                        id = "call_1",
                        name = "unknown_tool",
                        input = buildJsonObject { },
                    ),
                ),
                stop_reason = "tool_use",
                usage = ClaudeUsage(input_tokens = 10, output_tokens = 5),
            )
        )
        claudeClient.enqueueSendMessageResponse(
            ClaudeResponse(
                id = "msg_2",
                content = listOf(ContentBlock(type = "text", text = "알 수 없는 도구입니다.")),
                stop_reason = "end_turn",
                usage = ClaudeUsage(input_tokens = 10, output_tokens = 5),
            )
        )

        // When
        val events = collectEvents { emit ->
            service.chat(userId.toString(), ChatRequest(message = "뭔가 해줘"), emit)
        }

        // Then
        assertTrue(events.any { it.type == "done" })
    }

    // ── 조회 메서드 테스트 ──

    @Test
    fun `대화 목록을 페이지네이션으로 조회한다`() = runTest {
        // Given — 대화 3개 생성
        repeat(3) {
            service.chat(userId.toString(), ChatRequest(message = "대화 $it")) {}
        }

        // When
        val page1 = service.getConversations(userId.toString(), limit = 2, offset = 0)
        val page2 = service.getConversations(userId.toString(), limit = 2, offset = 2)

        // Then
        assertEquals(2, page1.size)
        assertEquals(1, page2.size)
    }

    @Test
    fun `대화 목록이 없으면 빈 리스트를 반환한다`() {
        // When
        val conversations = service.getConversations(userId.toString(), limit = 20, offset = 0)

        // Then
        assertTrue(conversations.isEmpty())
    }

    @Test
    fun `대화 상세를 정상 조회한다`() = runTest {
        // Given
        val events = collectEvents { emit ->
            service.chat(userId.toString(), ChatRequest(message = "안녕"), emit)
        }
        val convId = events.find { it.type == "done" }!!.conversationId!!

        // When
        val detail = service.getConversationDetail(userId.toString(), convId)

        // Then
        assertEquals(2, detail.messages.size)
        assertEquals("user", detail.messages[0].role)
        assertEquals("assistant", detail.messages[1].role)
    }

    @Test
    fun `사용량을 조회한다`() = runTest {
        // Given — 채팅 2회 수행
        service.chat(userId.toString(), ChatRequest(message = "안녕")) {}
        service.chat(userId.toString(), ChatRequest(message = "또 안녕")) {}

        // When
        val today = Clock.System.todayIn(TimeZone.UTC)
        val usage = service.getUsage(userId.toString(), today)

        // Then
        assertEquals(2, usage.requestCount)
        assertTrue(usage.inputTokens > 0)
        assertTrue(usage.outputTokens > 0)
    }

    @Test
    fun `날짜를 지정하지 않으면 오늘 사용량을 반환한다`() = runTest {
        // Given
        service.chat(userId.toString(), ChatRequest(message = "안녕")) {}

        // When
        val usage = service.getUsage(userId.toString(), date = null)

        // Then
        assertEquals(1, usage.requestCount)
        assertEquals(Clock.System.todayIn(TimeZone.UTC).toString(), usage.date)
    }
}
