package org.bmsk.lifemash.calendar.data.repository

import org.bmsk.lifemash.calendar.data.api.CalendarApi
import org.bmsk.lifemash.calendar.data.api.dto.CreateCommentBody
import org.bmsk.lifemash.calendar.domain.model.Comment
import org.bmsk.lifemash.calendar.domain.repository.CommentRepository

internal class CommentRepositoryImpl(private val api: CalendarApi) : CommentRepository {

    override suspend fun getComments(groupId: String, eventId: String): List<Comment> =
        api.getComments(groupId, eventId).map { it.toDomain() }

    override suspend fun createComment(groupId: String, eventId: String, content: String): Comment =
        api.createComment(groupId, eventId, CreateCommentBody(content)).toDomain()

    override suspend fun deleteComment(groupId: String, eventId: String, commentId: String) =
        api.deleteComment(groupId, eventId, commentId)
}
