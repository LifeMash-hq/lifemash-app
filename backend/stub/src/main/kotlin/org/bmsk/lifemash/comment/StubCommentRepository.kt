package org.bmsk.lifemash.comment

import kotlinx.datetime.Instant
import org.bmsk.lifemash.model.calendar.CommentDto
import java.util.*

class StubCommentRepository : CommentRepository {
    private val epoch = Instant.fromEpochSeconds(0)
    private val comments = mutableMapOf<UUID, CommentDto>()

    override fun getByEventId(eventId: UUID): List<CommentDto> =
        comments.values.filter { it.eventId == eventId.toString() }

    override fun create(eventId: UUID, authorId: UUID, content: String): CommentDto {
        val id = UUID.randomUUID()
        val comment = CommentDto(
            id = id.toString(),
            eventId = eventId.toString(),
            authorId = authorId.toString(),
            content = content,
            createdAt = epoch,
        )
        comments[id] = comment
        return comment
    }

    override fun delete(commentId: UUID, authorId: UUID) {
        comments.remove(commentId)
    }
}
