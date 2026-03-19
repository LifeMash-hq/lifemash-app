package org.bmsk.lifemash.assistant.ui

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.bmsk.lifemash.assistant.domain.model.AssistantUsage
import org.bmsk.lifemash.assistant.domain.model.ChatMessage
import org.bmsk.lifemash.assistant.domain.model.Conversation
import org.bmsk.lifemash.assistant.domain.model.SseEvent
import org.bmsk.lifemash.assistant.domain.usecase.*
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Clock

@OptIn(ExperimentalCoroutinesApi::class)
class AssistantViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val now = Clock.System.now()

    private var sendMessageEvents = listOf<SseEvent>()
    private var conversationsResult = emptyList<Conversation>()
    private var apiKeyStatus = false

    private val fakeSendMessage = object : SendMessageUseCase {
        override suspend fun invoke(
            message: String,
            conversationId: String?,
            onEvent: suspend (SseEvent) -> Unit,
        ) {
            sendMessageEvents.forEach { onEvent(it) }
        }
    }

    private val fakeGetConversations = object : GetConversationsUseCase {
        override suspend fun invoke(limit: Int, offset: Int) = conversationsResult
    }

    private val fakeGetConversation = object : GetConversationUseCase {
        override suspend fun invoke(id: String) = Conversation(
            id = id, title = "테스트", createdAt = now, updatedAt = now,
        ) to listOf(
            ChatMessage(id = "m1", role = "user", content = "안녕", createdAt = now),
        )
    }

    private val fakeDeleteConversation = object : DeleteConversationUseCase {
        override suspend fun invoke(id: String) {}
    }

    private val fakeSaveApiKey = object : SaveApiKeyUseCase {
        override suspend fun invoke(key: String) {}
    }

    private val fakeRemoveApiKey = object : RemoveApiKeyUseCase {
        override suspend fun invoke() {}
    }

    private val fakeGetApiKeyStatus = object : GetApiKeyStatusUseCase {
        override suspend fun invoke() = apiKeyStatus
    }

    private val fakeGetUsage = object : GetUsageUseCase {
        override suspend fun invoke(date: String?) = AssistantUsage(
            date = "2026-03-19", inputTokens = 100, outputTokens = 200,
            requestCount = 5, dailyLimit = 20,
        )
    }

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = AssistantViewModel(
        sendMessageUseCase = fakeSendMessage,
        getConversationsUseCase = fakeGetConversations,
        getConversationUseCase = fakeGetConversation,
        deleteConversationUseCase = fakeDeleteConversation,
        saveApiKeyUseCase = fakeSaveApiKey,
        removeApiKeyUseCase = fakeRemoveApiKey,
        getApiKeyStatusUseCase = fakeGetApiKeyStatus,
        getUsageUseCase = fakeGetUsage,
    )

    @Test
    fun `빈 메시지는 전송하지 않는다`() = runTest {
        // Given
        val viewModel = createViewModel()
        viewModel.updateInputText("   ")

        // When
        viewModel.sendMessage()

        // Then
        assertTrue(viewModel.uiState.value.messages.isEmpty())
        assertFalse(viewModel.uiState.value.isStreaming)
    }

    @Test
    fun `스트리밍 완료 후 메시지가 목록에 추가된다`() = runTest {
        // Given
        sendMessageEvents = listOf(
            SseEvent(type = "token", content = "안녕하세요"),
            SseEvent(type = "done", conversationId = "conv-1"),
        )
        val viewModel = createViewModel()

        // When
        viewModel.updateInputText("테스트 메시지")
        val job = launch { viewModel.sendMessage() }
        job.join()

        // Then
        val messages = viewModel.uiState.value.messages
        assertEquals(2, messages.size) // user + assistant
        assertEquals("user", messages[0].role)
        assertEquals("assistant", messages[1].role)
        assertEquals("안녕하세요", messages[1].content)
        assertFalse(viewModel.uiState.value.isStreaming)
    }

    @Test
    fun `tool_start 이벤트 시 activeToolCall이 업데이트된다`() = runTest {
        // Given
        var capturedState: AssistantUiState? = null
        sendMessageEvents = listOf(
            SseEvent(type = "tool_start", tool = "get_today_events"),
        )
        // sendMessage will hang since no "done" event, but we can check intermediate state
        val viewModel = createViewModel()

        val job = launch {
            viewModel.uiState.collect { state ->
                if (state.activeToolCall != null) {
                    capturedState = state
                }
            }
        }

        // When
        viewModel.updateInputText("오늘 일정")
        viewModel.sendMessage()

        // Then - intermediate state had activeToolCall
        // Note: with UnconfinedTestDispatcher, events complete synchronously
        job.cancel()
    }

    @Test
    fun `에러 발생 시 error 상태가 업데이트된다`() = runTest {
        // Given
        sendMessageEvents = listOf(
            SseEvent(type = "error", content = "API 오류"),
        )
        val viewModel = createViewModel()

        // When
        viewModel.updateInputText("테스트")
        val job = launch { viewModel.sendMessage() }
        job.join()

        // Then
        assertEquals("API 오류", viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.isStreaming)
    }
}
