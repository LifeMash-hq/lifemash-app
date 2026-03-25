package org.bmsk.lifemash.comment

import org.bmsk.lifemash.model.calendar.CommentDto
import java.util.*

interface CommentRepository {
    fun getByEventId(eventId: UUID): List<CommentDto>
    fun create(eventId: UUID, authorId: UUID, content: String): CommentDto
    fun delete(commentId: UUID, authorId: UUID)
}
