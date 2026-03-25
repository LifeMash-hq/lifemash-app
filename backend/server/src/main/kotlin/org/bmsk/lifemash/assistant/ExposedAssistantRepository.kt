package org.bmsk.lifemash.assistant

import org.bmsk.lifemash.model.assistant.ConversationDetailDto
import org.bmsk.lifemash.model.assistant.ConversationDto
import org.bmsk.lifemash.model.assistant.MessageDto
import org.bmsk.lifemash.util.nowUtc
import org.bmsk.lifemash.util.toKotlinxInstant
import org.bmsk.lifemash.db.tables.AssistantConversations
import org.bmsk.lifemash.db.tables.AssistantMessages
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*

class ExposedAssistantRepository : AssistantRepository {

    override fun createConversation(userId: UUID, title: String): ConversationDto = transaction {
        val now = now()
        val row = AssistantConversations.insert {
            it[AssistantConversations.userId] = userId
            it[AssistantConversations.title] = title
            it[createdAt] = now
            it[updatedAt] = now
        }.resultedValues!!.first()
        row.toConversationDto()
    }

    override fun getConversations(userId: UUID, limit: Int, offset: Long): List<ConversationDto> = transaction {
        AssistantConversations.selectAll()
            .where { AssistantConversations.userId eq userId }
            .orderBy(AssistantConversations.updatedAt, SortOrder.DESC)
            .limit(limit)
            .offset(offset)
            .map { it.toConversationDto() }
    }

    override fun getConversation(conversationId: UUID): ConversationDto? = transaction {
        AssistantConversations.selectAll()
            .where { AssistantConversations.id eq conversationId }
            .singleOrNull()?.toConversationDto()
    }

    override fun getConversationDetail(conversationId: UUID): ConversationDetailDto? = transaction {
        val conv = AssistantConversations.selectAll()
            .where { AssistantConversations.id eq conversationId }
            .singleOrNull() ?: return@transaction null

        val messages = AssistantMessages.selectAll()
            .where { AssistantMessages.conversationId eq conversationId }
            .orderBy(AssistantMessages.createdAt)
            .map { it.toMessageDto() }

        ConversationDetailDto(
            id = conv[AssistantConversations.id].toString(),
            title = conv[AssistantConversations.title],
            messages = messages,
            createdAt = conv[AssistantConversations.createdAt].toKotlinxInstant(),
        )
    }

    override fun deleteConversation(conversationId: UUID, userId: UUID): Boolean = transaction {
        val deleted = AssistantConversations.deleteWhere {
            (AssistantConversations.id eq conversationId) and (AssistantConversations.userId eq userId)
        }
        deleted > 0
    }

    override fun isConversationOwner(conversationId: UUID, userId: UUID): Boolean = transaction {
        AssistantConversations.selectAll().where {
            (AssistantConversations.id eq conversationId) and (AssistantConversations.userId eq userId)
        }.singleOrNull() != null
    }

    override fun updateConversationTimestamp(conversationId: UUID) = transaction {
        AssistantConversations.update({ AssistantConversations.id eq conversationId }) {
            it[updatedAt] = now()
        }
        Unit
    }

    override fun addMessage(conversationId: UUID, role: String, content: String, toolCallsJson: String?): MessageDto =
        transaction {
            val row = AssistantMessages.insert {
                it[AssistantMessages.conversationId] = conversationId
                it[AssistantMessages.role] = role
                it[AssistantMessages.content] = content
                it[AssistantMessages.toolCallsJson] = toolCallsJson
                it[createdAt] = now()
            }.resultedValues!!.first()
            row.toMessageDto()
        }

    override fun getRecentMessages(conversationId: UUID, limit: Int): List<MessageDto> = transaction {
        AssistantMessages.selectAll()
            .where { AssistantMessages.conversationId eq conversationId }
            .orderBy(AssistantMessages.createdAt, SortOrder.DESC)
            .limit(limit)
            .reversed()
            .map { it.toMessageDto() }
    }

    private fun ResultRow.toConversationDto() = ConversationDto(
        id = this[AssistantConversations.id].toString(),
        title = this[AssistantConversations.title],
        createdAt = this[AssistantConversations.createdAt].toKotlinxInstant(),
        updatedAt = this[AssistantConversations.updatedAt].toKotlinxInstant(),
    )

    private fun ResultRow.toMessageDto() = MessageDto(
        id = this[AssistantMessages.id].toString(),
        role = this[AssistantMessages.role],
        content = this[AssistantMessages.content],
        createdAt = this[AssistantMessages.createdAt].toKotlinxInstant(),
    )

    private fun now(): OffsetDateTime =
        nowUtc()
}
