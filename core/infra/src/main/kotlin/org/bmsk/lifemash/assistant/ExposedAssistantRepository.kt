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
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid

class ExposedAssistantRepository : AssistantRepository {

    override fun createConversation(userId: Uuid, title: String): ConversationDto = transaction {
        val now = now()
        val row = AssistantConversations.insert {
            it[AssistantConversations.userId] = userId.toJavaUuid()
            it[createdAt] = now
            it[updatedAt] = now
            it[AssistantConversations.title] = title
        }.resultedValues!!.first()
        row.toConversationDto()
    }

    override fun getConversations(userId: Uuid, limit: Int, offset: Long): List<ConversationDto> = transaction {
        AssistantConversations.selectAll()
            .where { AssistantConversations.userId eq userId.toJavaUuid() }
            .orderBy(AssistantConversations.updatedAt, SortOrder.DESC)
            .limit(limit)
            .offset(offset)
            .map { it.toConversationDto() }
    }

    override fun getConversation(conversationId: Uuid): ConversationDto? = transaction {
        AssistantConversations.selectAll()
            .where { AssistantConversations.id eq conversationId.toJavaUuid() }
            .singleOrNull()?.toConversationDto()
    }

    override fun getConversationDetail(conversationId: Uuid): ConversationDetailDto? = transaction {
        val conv = AssistantConversations.selectAll()
            .where { AssistantConversations.id eq conversationId.toJavaUuid() }
            .singleOrNull() ?: return@transaction null

        val messages = AssistantMessages.selectAll()
            .where { AssistantMessages.conversationId eq conversationId.toJavaUuid() }
            .orderBy(AssistantMessages.createdAt)
            .map { it.toMessageDto() }

        ConversationDetailDto(
            id = conv[AssistantConversations.id].toString(),
            title = conv[AssistantConversations.title],
            messages = messages,
            createdAt = conv[AssistantConversations.createdAt].toKotlinxInstant(),
        )
    }

    override fun deleteConversation(conversationId: Uuid, userId: Uuid): Boolean = transaction {
        val deleted = AssistantConversations.deleteWhere {
            (AssistantConversations.id eq conversationId.toJavaUuid()) and (AssistantConversations.userId eq userId.toJavaUuid())
        }
        deleted > 0
    }

    override fun isConversationOwner(conversationId: Uuid, userId: Uuid): Boolean = transaction {
        AssistantConversations.selectAll().where {
            (AssistantConversations.id eq conversationId.toJavaUuid()) and (AssistantConversations.userId eq userId.toJavaUuid())
        }.singleOrNull() != null
    }

    override fun updateConversationTimestamp(conversationId: Uuid) = transaction {
        AssistantConversations.update({ AssistantConversations.id eq conversationId.toJavaUuid() }) {
            it[updatedAt] = now()
        }
        Unit
    }

    override fun addMessage(conversationId: Uuid, role: String, content: String, toolCallsJson: String?): MessageDto =
        transaction {
            val row = AssistantMessages.insert {
                it[AssistantMessages.conversationId] = conversationId.toJavaUuid()
                it[AssistantMessages.role] = role
                it[AssistantMessages.content] = content
                it[AssistantMessages.toolCallsJson] = toolCallsJson
                it[createdAt] = now()
            }.resultedValues!!.first()
            row.toMessageDto()
        }

    override fun getRecentMessages(conversationId: Uuid, limit: Int): List<MessageDto> = transaction {
        AssistantMessages.selectAll()
            .where { AssistantMessages.conversationId eq conversationId.toJavaUuid() }
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
