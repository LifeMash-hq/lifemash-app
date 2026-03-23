package org.bmsk.lifemash.assistant.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock
import org.bmsk.lifemash.assistant.domain.model.InstalledBlock
import org.bmsk.lifemash.assistant.domain.usecase.*
import org.bmsk.lifemash.home.api.HomeBlock
import org.bmsk.lifemash.home.domain.repository.HomeLayoutRepository

internal class AssistantViewModel(
    private val sendMessageUseCase: SendMessageUseCase,
    private val getConversationsUseCase: GetConversationsUseCase,
    private val getConversationUseCase: GetConversationUseCase,
    private val deleteConversationUseCase: DeleteConversationUseCase,
    private val saveApiKeyUseCase: SaveApiKeyUseCase,
    private val removeApiKeyUseCase: RemoveApiKeyUseCase,
    private val getApiKeyStatusUseCase: GetApiKeyStatusUseCase,
    private val getUsageUseCase: GetUsageUseCase,
    private val homeLayoutRepository: HomeLayoutRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<AssistantUiState>(AssistantUiState.Loading)
    val uiState: StateFlow<AssistantUiState> = _uiState.asStateFlow()

    fun updateInputText(text: String) {
        updateReady { copy(inputText = text) }
    }

    fun sendMessage() {
        val ready = _uiState.value as? AssistantUiState.Ready ?: return
        val message = ready.inputText.trim()
        if (message.isEmpty() || ready.isStreaming) return

        val conversationId = ready.currentConversationId
        val userMessage = ChatMessageUi(
            id = "user_${Clock.System.now().toEpochMilliseconds()}",
            role = "user",
            content = message,
        )

        updateReady {
            copy(
                messages = (messages + userMessage).toPersistentList(),
                inputText = "",
                isStreaming = true,
                streamingText = "",
                activeToolCall = null,
                error = null,
            )
        }

        viewModelScope.launch {
            val installedBlocks = homeLayoutRepository.getLayout().first()
                .filterIsInstance<HomeBlock.WebViewBlock>()
                .map { InstalledBlock(id = it.blockId, url = it.url) }

            runCatching {
                sendMessageUseCase(
                    message = message,
                    conversationId = conversationId,
                    installedBlocks = installedBlocks,
                ) { event ->
                    when (event.type) {
                        "token" -> {
                            updateReady {
                                copy(streamingText = streamingText + (event.content ?: ""))
                            }
                        }
                        "tool_start" -> {
                            val toolLabel = when (event.tool) {
                                "get_today_events" -> "캘린더 조회 중..."
                                "get_month_events" -> "월간 일정 조회 중..."
                                "get_my_groups" -> "그룹 조회 중..."
                                "create_event" -> "일정 생성 중..."
                                else -> "${event.tool} 실행 중..."
                            }
                            updateReady { copy(activeToolCall = toolLabel) }
                        }
                        "tool_end" -> {
                            updateReady { copy(activeToolCall = null) }
                        }
                        "done" -> {
                            updateReady {
                                val assistantMessage = ChatMessageUi(
                                    id = "assistant_${Clock.System.now().toEpochMilliseconds()}",
                                    role = "assistant",
                                    content = streamingText,
                                )
                                copy(
                                    messages = (messages + assistantMessage).toPersistentList(),
                                    isStreaming = false,
                                    streamingText = "",
                                    currentConversationId = event.conversationId ?: currentConversationId,
                                )
                            }
                        }
                        "error" -> {
                            updateReady {
                                val updatedMessages = if (streamingText.isNotBlank()) {
                                    val partial = ChatMessageUi(
                                        id = "assistant_${Clock.System.now().toEpochMilliseconds()}",
                                        role = "assistant",
                                        content = streamingText,
                                    )
                                    (messages + partial).toPersistentList()
                                } else {
                                    messages
                                }
                                copy(
                                    messages = updatedMessages,
                                    isStreaming = false,
                                    streamingText = "",
                                    error = event.content ?: "오류가 발생했습니다.",
                                )
                            }
                        }
                    }
                }
            }.onFailure { e ->
                updateReady {
                    val updatedMessages = if (streamingText.isNotBlank()) {
                        val partial = ChatMessageUi(
                            id = "assistant_${Clock.System.now().toEpochMilliseconds()}",
                            role = "assistant",
                            content = streamingText,
                        )
                        (messages + partial).toPersistentList()
                    } else {
                        messages
                    }
                    copy(
                        messages = updatedMessages,
                        isStreaming = false,
                        streamingText = "",
                        error = e.message ?: "네트워크 오류가 발생했습니다.",
                    )
                }
            }
        }
    }

    fun loadConversations() {
        viewModelScope.launch {
            runCatching { getConversationsUseCase() }
                .onSuccess { convos ->
                    updateReady {
                        copy(
                            conversations = convos.map { c ->
                                ConversationUi(id = c.id, title = c.title)
                            }.toPersistentList(),
                        )
                    }
                }
                .onFailure { e ->
                    updateReady { copy(error = e.message) }
                }
        }
    }

    fun loadConversation(id: String) {
        viewModelScope.launch {
            runCatching { getConversationUseCase(id) }
                .onSuccess { (_, messages) ->
                    updateReady {
                        copy(
                            currentConversationId = id,
                            messages = messages.map { m ->
                                ChatMessageUi(id = m.id, role = m.role, content = m.content)
                            }.toPersistentList(),
                            showConversationList = false,
                        )
                    }
                }
                .onFailure { e ->
                    updateReady { copy(error = e.message) }
                }
        }
    }

    fun deleteConversation(id: String) {
        viewModelScope.launch {
            runCatching { deleteConversationUseCase(id) }
                .onSuccess { loadConversations() }
                .onFailure { e ->
                    updateReady { copy(error = e.message) }
                }
        }
    }

    fun newConversation() {
        updateReady {
            copy(
                currentConversationId = null,
                messages = persistentListOf(),
                showConversationList = false,
            )
        }
    }

    fun toggleConversationList() {
        var shouldLoad = false
        updateReady {
            val show = !showConversationList
            shouldLoad = show
            copy(showConversationList = show)
        }
        if (shouldLoad) loadConversations()
    }

    fun toggleSettings() {
        var shouldLoad = false
        updateReady {
            val show = !showSettings
            shouldLoad = show
            copy(showSettings = show)
        }
        if (shouldLoad) loadUsage()
    }

    fun saveApiKey(key: String) {
        viewModelScope.launch {
            runCatching { saveApiKeyUseCase(key) }
                .onSuccess {
                    updateReady { copy(hasApiKey = true) }
                }
                .onFailure { e ->
                    updateReady { copy(error = e.message) }
                }
        }
    }

    fun removeApiKey() {
        viewModelScope.launch {
            runCatching { removeApiKeyUseCase() }
                .onSuccess {
                    updateReady { copy(hasApiKey = false) }
                }
                .onFailure { e ->
                    updateReady { copy(error = e.message) }
                }
        }
    }

    fun clearError() {
        updateReady { copy(error = null) }
    }

    internal fun loadApiKeyStatus() {
        viewModelScope.launch {
            runCatching { getApiKeyStatusUseCase() }
                .onSuccess { hasKey ->
                    _uiState.value = AssistantUiState.Ready(hasApiKey = hasKey)
                }
                .onFailure {
                    _uiState.value = AssistantUiState.Ready(hasApiKey = false)
                }
        }
    }

    private fun loadUsage() {
        viewModelScope.launch {
            runCatching { getUsageUseCase() }
                .onSuccess { usage ->
                    updateReady { copy(usage = usage) }
                }
        }
    }

    private fun updateReady(transform: AssistantUiState.Ready.() -> AssistantUiState.Ready) {
        _uiState.update { state ->
            when (state) {
                is AssistantUiState.Ready -> state.transform()
                else -> state
            }
        }
    }
}
