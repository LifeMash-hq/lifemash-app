package org.bmsk.lifemash.assistant.domain.repository

import org.bmsk.lifemash.assistant.domain.model.AssistantUsage
import org.bmsk.lifemash.assistant.domain.model.ChatMessage
import org.bmsk.lifemash.assistant.domain.model.Conversation
import org.bmsk.lifemash.assistant.domain.model.InstalledBlock
import org.bmsk.lifemash.assistant.domain.model.SseEvent

interface AssistantRepository {
    suspend fun sendMessage(
        message: String,
        conversationId: String?,
        installedBlocks: List<InstalledBlock>,
        onEvent: suspend (SseEvent) -> Unit,
    )
    suspend fun getConversations(limit: Int, offset: Int): List<Conversation>
    suspend fun getConversation(id: String): Pair<Conversation, List<ChatMessage>>
    suspend fun deleteConversation(id: String)
    suspend fun getUsage(date: String?): AssistantUsage
}
