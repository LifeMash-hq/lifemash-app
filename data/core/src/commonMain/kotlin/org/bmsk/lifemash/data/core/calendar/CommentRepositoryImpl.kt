package org.bmsk.lifemash.data.core.calendar

import org.bmsk.lifemash.domain.calendar.Comment
import org.bmsk.lifemash.domain.calendar.CommentRepository
import org.bmsk.lifemash.data.remote.calendar.CalendarApi
import org.bmsk.lifemash.data.remote.calendar.dto.CreateCommentRequest

internal class CommentRepositoryImpl(private val api: CalendarApi) : CommentRepository {

    override suspend fun getComments(groupId: String, eventId: String): List<Comment> =
        api.getComments(groupId, eventId).map { it.toDomain() }

    override suspend fun createComment(
        groupId: String,
        eventId: String,
        content: String,
    ): Comment =
        api.createComment(
            groupId,
            eventId,
            CreateCommentRequest(content),
        ).toDomain()

    override suspend fun deleteComment(
        groupId: String,
        eventId: String,
        commentId: String,
    ) =
        api.deleteComment(
            groupId,
            eventId,
            commentId,
        )
}
