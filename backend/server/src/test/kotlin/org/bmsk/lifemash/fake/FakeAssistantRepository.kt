package org.bmsk.lifemash.fake

import kotlin.time.Clock
import org.bmsk.lifemash.assistant.*
import org.bmsk.lifemash.model.assistant.ConversationDetailDto
import org.bmsk.lifemash.model.assistant.ConversationDto
import org.bmsk.lifemash.model.assistant.MessageDto
import java.util.*

class FakeAssistantRepository : AssistantRepository {
    private val conversations = mutableMapOf<UUID, ConvData>()
    private val messages = mutableMapOf<UUID, MutableList<MessageDto>>()

    data class ConvData(val id: UUID, val userId: UUID, val title: String, val createdAt: kotlinx.datetime.Instant, var updatedAt: kotlinx.datetime.Instant)

    override fun createConversation(userId: UUID, title: String): ConversationDto {
        val id = UUID.randomUUID()
        val now = Clock.System.now()
        conversations[id] = ConvData(id, userId, title, now, now)
        messages[id] = mutableListOf()
        return ConversationDto(id.toString(), title, now, now)
    }

    override fun getConversations(userId: UUID, limit: Int, offset: Long): List<ConversationDto> =
        conversations.values
            .filter { it.userId == userId }
            .sortedByDescending { it.updatedAt }
            .drop(offset.toInt())
            .take(limit)
            .map { ConversationDto(it.id.toString(), it.title, it.createdAt, it.updatedAt) }

    override fun getConversation(conversationId: UUID): ConversationDto? =
        conversations[conversationId]?.let { ConversationDto(it.id.toString(), it.title, it.createdAt, it.updatedAt) }

    override fun getConversationDetail(conversationId: UUID): ConversationDetailDto? {
        val conv = conversations[conversationId] ?: return null
        return ConversationDetailDto(
            id = conv.id.toString(),
            title = conv.title,
            messages = messages[conversationId] ?: emptyList(),
            createdAt = conv.createdAt,
        )
    }

    override fun deleteConversation(conversationId: UUID, userId: UUID): Boolean {
        val conv = conversations[conversationId] ?: return false
        if (conv.userId != userId) return false
        conversations.remove(conversationId)
        messages.remove(conversationId)
        return true
    }

    override fun isConversationOwner(conversationId: UUID, userId: UUID): Boolean =
        conversations[conversationId]?.userId == userId

    override fun updateConversationTimestamp(conversationId: UUID) {
        conversations[conversationId]?.updatedAt = Clock.System.now()
    }

    override fun addMessage(conversationId: UUID, role: String, content: String, toolCallsJson: String?): MessageDto {
        val id = UUID.randomUUID()
        val msg = MessageDto(id.toString(), role, content, Clock.System.now())
        messages.getOrPut(conversationId) { mutableListOf() }.add(msg)
        return msg
    }

    override fun getRecentMessages(conversationId: UUID, limit: Int): List<MessageDto> =
        (messages[conversationId] ?: emptyList()).takeLast(limit)

    fun getMessagesFor(conversationId: String): List<MessageDto> =
        messages[UUID.fromString(conversationId)] ?: emptyList()
}
