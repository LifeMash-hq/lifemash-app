package org.bmsk.lifemash.comment

import org.bmsk.lifemash.model.calendar.CommentDto
import org.bmsk.lifemash.model.calendar.CreateCommentRequest

interface CommentService {
    fun getComments(userId: String, eventId: String): List<CommentDto>
    fun create(userId: String, eventId: String, request: CreateCommentRequest): CommentDto
    fun delete(userId: String, eventId: String, commentId: String)
}
