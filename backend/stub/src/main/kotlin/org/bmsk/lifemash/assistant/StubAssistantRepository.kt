package org.bmsk.lifemash.assistant

import kotlinx.datetime.Instant
import org.bmsk.lifemash.model.assistant.ConversationDetailDto
import org.bmsk.lifemash.model.assistant.ConversationDto
import org.bmsk.lifemash.model.assistant.MessageDto
import java.util.*

class StubAssistantRepository : AssistantRepository {
    private val epoch = Instant.fromEpochSeconds(0)
    private val conversations = mutableMapOf<UUID, ConversationDto>()
    private val messages = mutableMapOf<UUID, MutableList<MessageDto>>()

    override fun createConversation(userId: UUID, title: String): ConversationDto {
        val id = UUID.randomUUID()
        val conv = ConversationDto(
            id = id.toString(),
            title = title,
            createdAt = epoch,
            updatedAt = epoch,
        )
        conversations[id] = conv
        messages[id] = mutableListOf()
        return conv
    }

    override fun getConversations(userId: UUID, limit: Int, offset: Long): List<ConversationDto> =
        conversations.values.toList()

    override fun getConversation(conversationId: UUID): ConversationDto? =
        conversations[conversationId]

    override fun getConversationDetail(conversationId: UUID): ConversationDetailDto? {
        val conv = conversations[conversationId] ?: return null
        return ConversationDetailDto(
            id = conv.id,
            title = conv.title,
            messages = messages[conversationId] ?: emptyList(),
            createdAt = conv.createdAt,
        )
    }

    override fun deleteConversation(conversationId: UUID, userId: UUID): Boolean {
        conversations.remove(conversationId)
        messages.remove(conversationId)
        return true
    }

    override fun isConversationOwner(conversationId: UUID, userId: UUID): Boolean = true

    override fun updateConversationTimestamp(conversationId: UUID) {}

    override fun addMessage(conversationId: UUID, role: String, content: String, toolCallsJson: String?): MessageDto {
        val id = UUID.randomUUID()
        val msg = MessageDto(
            id = id.toString(),
            role = role,
            content = content,
            createdAt = epoch,
        )
        messages.getOrPut(conversationId) { mutableListOf() }.add(msg)
        return msg
    }

    override fun getRecentMessages(conversationId: UUID, limit: Int): List<MessageDto> =
        (messages[conversationId] ?: emptyList()).takeLast(limit)
}
