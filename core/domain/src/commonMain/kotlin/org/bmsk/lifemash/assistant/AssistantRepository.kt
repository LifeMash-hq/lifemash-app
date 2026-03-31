package org.bmsk.lifemash.assistant

import org.bmsk.lifemash.model.assistant.ConversationDetailDto
import org.bmsk.lifemash.model.assistant.ConversationDto
import org.bmsk.lifemash.model.assistant.MessageDto
import kotlin.uuid.Uuid

interface AssistantRepository {
    fun createConversation(userId: Uuid, title: String): ConversationDto
    fun getConversations(userId: Uuid, limit: Int, offset: Long): List<ConversationDto>
    fun getConversation(conversationId: Uuid): ConversationDto?
    fun getConversationDetail(conversationId: Uuid): ConversationDetailDto?
    fun deleteConversation(conversationId: Uuid, userId: Uuid): Boolean
    fun isConversationOwner(conversationId: Uuid, userId: Uuid): Boolean
    fun updateConversationTimestamp(conversationId: Uuid)
    fun addMessage(conversationId: Uuid, role: String, content: String, toolCallsJson: String? = null): MessageDto
    fun getRecentMessages(conversationId: Uuid, limit: Int = 20): List<MessageDto>
}
