package org.bmsk.lifemash.fake

import kotlin.time.Clock
import org.bmsk.lifemash.model.calendar.CommentDto
import org.bmsk.lifemash.comment.CommentRepository
import org.bmsk.lifemash.plugins.ForbiddenException
import org.bmsk.lifemash.plugins.NotFoundException
import java.util.*

class FakeCommentRepository : CommentRepository {
    private val comments = mutableMapOf<UUID, CommentDto>()

    override fun getByEventId(eventId: UUID): List<CommentDto> =
        comments.values.filter { it.eventId == eventId.toString() }.sortedBy { it.createdAt }

    override fun create(eventId: UUID, authorId: UUID, content: String): CommentDto {
        val id = UUID.randomUUID()
        val comment = CommentDto(
            id = id.toString(),
            eventId = eventId.toString(),
            authorId = authorId.toString(),
            content = content,
            createdAt = Clock.System.now(),
        )
        comments[id] = comment
        return comment
    }

    override fun delete(commentId: UUID, authorId: UUID) {
        val comment = comments[commentId]
            ?: throw NotFoundException("Comment not found")
        if (comment.authorId != authorId.toString()) {
            throw ForbiddenException("Only author can delete comment")
        }
        comments.remove(commentId)
    }
}
