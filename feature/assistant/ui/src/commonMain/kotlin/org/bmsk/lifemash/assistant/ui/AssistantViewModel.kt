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

    private val _uiState = MutableStateFlow(AssistantUiState())
    val uiState: StateFlow<AssistantUiState> = _uiState.asStateFlow()

    init {
        loadApiKeyStatus()
    }

    fun updateInputText(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun sendMessage() {
        val message = _uiState.value.inputText.trim()
        if (message.isEmpty() || _uiState.value.isStreaming) return

        val userMessage = ChatMessageUi(
            id = "user_${Clock.System.now().toEpochMilliseconds()}",
            role = "user",
            content = message,
        )

        _uiState.update {
            it.copy(
                messages = (it.messages + userMessage).toPersistentList(),
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
                    conversationId = _uiState.value.currentConversationId,
                    installedBlocks = installedBlocks,
                ) { event ->
                    when (event.type) {
                        "token" -> {
                            _uiState.update {
                                it.copy(streamingText = it.streamingText + (event.content ?: ""))
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
                            _uiState.update { it.copy(activeToolCall = toolLabel) }
                        }
                        "tool_end" -> {
                            _uiState.update { it.copy(activeToolCall = null) }
                        }
                        "done" -> {
                            val assistantMessage = ChatMessageUi(
                                id = "assistant_${Clock.System.now().toEpochMilliseconds()}",
                                role = "assistant",
                                content = _uiState.value.streamingText,
                            )
                            _uiState.update {
                                it.copy(
                                    messages = (it.messages + assistantMessage).toPersistentList(),
                                    isStreaming = false,
                                    streamingText = "",
                                    currentConversationId = event.conversationId ?: it.currentConversationId,
                                )
                            }
                        }
                        "error" -> {
                            _uiState.update { state ->
                                // 이미 누적된 중간 텍스트가 있으면 메시지로 보존
                                val messages = if (state.streamingText.isNotBlank()) {
                                    val partial = ChatMessageUi(
                                        id = "assistant_${Clock.System.now().toEpochMilliseconds()}",
                                        role = "assistant",
                                        content = state.streamingText,
                                    )
                                    (state.messages + partial).toPersistentList()
                                } else {
                                    state.messages
                                }
                                state.copy(
                                    messages = messages,
                                    isStreaming = false,
                                    streamingText = "",
                                    error = event.content ?: "오류가 발생했습니다.",
                                )
                            }
                        }
                    }
                }
            }.onFailure { e ->
                _uiState.update { state ->
                    val messages = if (state.streamingText.isNotBlank()) {
                        val partial = ChatMessageUi(
                            id = "assistant_${Clock.System.now().toEpochMilliseconds()}",
                            role = "assistant",
                            content = state.streamingText,
                        )
                        (state.messages + partial).toPersistentList()
                    } else {
                        state.messages
                    }
                    state.copy(
                        messages = messages,
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
                    _uiState.update {
                        it.copy(
                            conversations = convos.map { c ->
                                ConversationUi(id = c.id, title = c.title)
                            }.toPersistentList(),
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.message) }
                }
        }
    }

    fun loadConversation(id: String) {
        viewModelScope.launch {
            runCatching { getConversationUseCase(id) }
                .onSuccess { (_, messages) ->
                    _uiState.update {
                        it.copy(
                            currentConversationId = id,
                            messages = messages.map { m ->
                                ChatMessageUi(id = m.id, role = m.role, content = m.content)
                            }.toPersistentList(),
                            showConversationList = false,
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.message) }
                }
        }
    }

    fun deleteConversation(id: String) {
        viewModelScope.launch {
            runCatching { deleteConversationUseCase(id) }
                .onSuccess { loadConversations() }
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.message) }
                }
        }
    }

    fun newConversation() {
        _uiState.update {
            it.copy(
                currentConversationId = null,
                messages = persistentListOf(),
                showConversationList = false,
            )
        }
    }

    fun toggleConversationList() {
        val show = !_uiState.value.showConversationList
        _uiState.update { it.copy(showConversationList = show) }
        if (show) loadConversations()
    }

    fun toggleSettings() {
        val show = !_uiState.value.showSettings
        _uiState.update { it.copy(showSettings = show) }
        if (show) loadUsage()
    }

    fun saveApiKey(key: String) {
        viewModelScope.launch {
            runCatching { saveApiKeyUseCase(key) }
                .onSuccess {
                    _uiState.update { it.copy(hasApiKey = true) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.message) }
                }
        }
    }

    fun removeApiKey() {
        viewModelScope.launch {
            runCatching { removeApiKeyUseCase() }
                .onSuccess {
                    _uiState.update { it.copy(hasApiKey = false) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.message) }
                }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    private fun loadApiKeyStatus() {
        viewModelScope.launch {
            runCatching { getApiKeyStatusUseCase() }
                .onSuccess { hasKey ->
                    _uiState.update { it.copy(hasApiKey = hasKey) }
                }
        }
    }

    private fun loadUsage() {
        viewModelScope.launch {
            runCatching { getUsageUseCase() }
                .onSuccess { usage ->
                    _uiState.update { it.copy(usage = usage) }
                }
        }
    }
}
