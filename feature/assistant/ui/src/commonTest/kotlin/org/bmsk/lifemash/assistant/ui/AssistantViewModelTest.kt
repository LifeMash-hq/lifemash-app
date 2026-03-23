package org.bmsk.lifemash.assistant.ui

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.bmsk.lifemash.assistant.domain.model.AssistantUsage
import org.bmsk.lifemash.assistant.domain.model.ChatMessage
import org.bmsk.lifemash.assistant.domain.model.Conversation
import org.bmsk.lifemash.assistant.domain.model.InstalledBlock
import org.bmsk.lifemash.assistant.domain.model.SseEvent
import org.bmsk.lifemash.assistant.domain.usecase.*
import org.bmsk.lifemash.home.api.HomeBlock
import org.bmsk.lifemash.home.domain.repository.HomeLayoutRepository
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
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
            installedBlocks: List<InstalledBlock>,
            onEvent: suspend (SseEvent) -> Unit,
        ) {
            sendMessageEvents.forEach { onEvent(it) }
        }
    }

    private val fakeHomeLayoutRepository = object : HomeLayoutRepository {
        override fun getLayout(): Flow<List<HomeBlock>> = flowOf(emptyList())
        override suspend fun saveLayout(blocks: List<HomeBlock>) {}
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

    private fun createViewModelRaw() = AssistantViewModel(
        sendMessageUseCase = fakeSendMessage,
        getConversationsUseCase = fakeGetConversations,
        getConversationUseCase = fakeGetConversation,
        deleteConversationUseCase = fakeDeleteConversation,
        saveApiKeyUseCase = fakeSaveApiKey,
        removeApiKeyUseCase = fakeRemoveApiKey,
        getApiKeyStatusUseCase = fakeGetApiKeyStatus,
        getUsageUseCase = fakeGetUsage,
        homeLayoutRepository = fakeHomeLayoutRepository,
    )

    private fun createViewModel(): AssistantViewModel {
        val vm = createViewModelRaw()
        vm.loadApiKeyStatus()
        return vm
    }

    @Test
    fun `초기 상태는 Loading이다`() = runTest {
        // Given
        val viewModel = createViewModelRaw()

        // Then
        assertIs<AssistantUiState.Loading>(viewModel.uiState.value)
    }

    @Test
    fun `loadApiKeyStatus 후 Ready 상태가 된다`() = runTest {
        // Given
        apiKeyStatus = true

        // When
        val viewModel = createViewModel()

        // Then
        val state = assertIs<AssistantUiState.Ready>(viewModel.uiState.value)
        assertTrue(state.hasApiKey)
    }

    @Test
    fun `빈 메시지는 전송하지 않는다`() = runTest {
        // Given
        val viewModel = createViewModel()
        viewModel.updateInputText("   ")

        // When
        viewModel.sendMessage()

        // Then
        val state = assertIs<AssistantUiState.Ready>(viewModel.uiState.value)
        assertTrue(state.messages.isEmpty())
        assertFalse(state.isStreaming)
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
        val state = assertIs<AssistantUiState.Ready>(viewModel.uiState.value)
        assertEquals(2, state.messages.size) // user + assistant
        assertEquals("user", state.messages[0].role)
        assertEquals("assistant", state.messages[1].role)
        assertEquals("안녕하세요", state.messages[1].content)
        assertFalse(state.isStreaming)
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
        val state = assertIs<AssistantUiState.Ready>(viewModel.uiState.value)
        assertEquals("API 오류", state.error)
        assertFalse(state.isStreaming)
    }
}
