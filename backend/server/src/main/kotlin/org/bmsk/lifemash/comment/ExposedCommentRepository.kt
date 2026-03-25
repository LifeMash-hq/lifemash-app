package org.bmsk.lifemash.comment

import org.bmsk.lifemash.model.calendar.CommentDto
import org.bmsk.lifemash.util.nowUtc
import org.bmsk.lifemash.util.toKotlinxInstant
import org.bmsk.lifemash.db.tables.Comments
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*

class ExposedCommentRepository : CommentRepository {

    override fun getByEventId(eventId: UUID): List<CommentDto> = transaction {
        Comments.selectAll().where { Comments.eventId eq eventId }
            .orderBy(Comments.createdAt)
            .map { it.toDto() }
    }

    override fun create(eventId: UUID, authorId: UUID, content: String): CommentDto = transaction {
        Comments.insert {
            it[Comments.eventId] = eventId
            it[Comments.authorId] = authorId
            it[Comments.content] = content
            it[Comments.createdAt] = nowUtc()
        }.resultedValues!!.first().toDto()
    }

    override fun delete(commentId: UUID, authorId: UUID) = transaction {
        val comment = Comments.selectAll().where { Comments.id eq commentId }.singleOrNull()
            ?: throw org.bmsk.lifemash.plugins.NotFoundException("Comment not found")

        if (comment[Comments.authorId] != authorId) {
            throw org.bmsk.lifemash.plugins.ForbiddenException("Only author can delete comment")
        }

        Comments.deleteWhere { Comments.id eq commentId }
        Unit
    }

    private fun ResultRow.toDto() = CommentDto(
        id = this[Comments.id].toString(),
        eventId = this[Comments.eventId].toString(),
        authorId = this[Comments.authorId].toString(),
        content = this[Comments.content],
        createdAt = this[Comments.createdAt].toKotlinxInstant(),
    )
}
