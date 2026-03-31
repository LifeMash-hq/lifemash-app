package org.bmsk.lifemash.comment

import org.bmsk.lifemash.model.calendar.CommentDto
import kotlin.uuid.Uuid

interface CommentRepository {
    fun getByEventId(eventId: Uuid): List<CommentDto>
    fun create(eventId: Uuid, authorId: Uuid, content: String): CommentDto
    fun delete(commentId: Uuid, authorId: Uuid)
}
