package org.bmsk.lifemash.assistant.ui

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import org.bmsk.lifemash.assistant.domain.model.AssistantUsage

internal sealed interface AssistantUiState {
    data object Loading : AssistantUiState
    data class Ready(
        val messages: PersistentList<ChatMessageUi> = persistentListOf(),
        val conversations: PersistentList<ConversationUi>? = null,
        val currentConversationId: String? = null,
        val isStreaming: Boolean = false,
        val streamingText: String = "",
        val activeToolCall: String? = null,
        val inputText: String = "",
        val error: String? = null,
        val hasApiKey: Boolean,
        val usage: AssistantUsage? = null,
        val showSettings: Boolean = false,
        val showConversationList: Boolean = false,
    ) : AssistantUiState
}

internal data class ChatMessageUi(
    val id: String,
    val role: String,
    val content: String,
)

internal data class ConversationUi(
    val id: String,
    val title: String,
)
