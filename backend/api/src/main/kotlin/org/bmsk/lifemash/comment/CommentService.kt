package org.bmsk.lifemash.comment

import org.bmsk.lifemash.model.calendar.CommentDto
import org.bmsk.lifemash.model.calendar.CreateCommentRequest

interface CommentService {
    fun getComments(groupId: String, userId: String, eventId: String): List<CommentDto>
    fun create(groupId: String, userId: String, eventId: String, request: CreateCommentRequest): CommentDto
    fun delete(groupId: String, userId: String, commentId: String)
}
