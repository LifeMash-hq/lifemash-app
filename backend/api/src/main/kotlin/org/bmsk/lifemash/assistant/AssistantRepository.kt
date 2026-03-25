package org.bmsk.lifemash.assistant

import org.bmsk.lifemash.model.assistant.ConversationDetailDto
import org.bmsk.lifemash.model.assistant.ConversationDto
import org.bmsk.lifemash.model.assistant.MessageDto
import java.util.*

interface AssistantRepository {
    fun createConversation(userId: UUID, title: String): ConversationDto
    fun getConversations(userId: UUID, limit: Int, offset: Long): List<ConversationDto>
    fun getConversation(conversationId: UUID): ConversationDto?
    fun getConversationDetail(conversationId: UUID): ConversationDetailDto?
    fun deleteConversation(conversationId: UUID, userId: UUID): Boolean
    fun isConversationOwner(conversationId: UUID, userId: UUID): Boolean
    fun updateConversationTimestamp(conversationId: UUID)
    fun addMessage(conversationId: UUID, role: String, content: String, toolCallsJson: String? = null): MessageDto
    fun getRecentMessages(conversationId: UUID, limit: Int = 20): List<MessageDto>
}
