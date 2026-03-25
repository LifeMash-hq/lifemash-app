package org.bmsk.lifemash.comment

import kotlinx.datetime.Instant
import org.bmsk.lifemash.model.calendar.CommentDto
import org.bmsk.lifemash.model.calendar.CreateCommentRequest

class StubCommentService : CommentService {
    private val epoch = Instant.fromEpochSeconds(0)

    override fun getComments(groupId: String, userId: String, eventId: String): List<CommentDto> =
        listOf(
            CommentDto(
                id = "demo-comment-1",
                eventId = eventId,
                authorId = userId,
                content = "Demo comment",
                createdAt = epoch,
            )
        )

    override fun create(groupId: String, userId: String, eventId: String, request: CreateCommentRequest): CommentDto =
        CommentDto(
            id = "demo-comment-new",
            eventId = eventId,
            authorId = userId,
            content = request.content,
            createdAt = epoch,
        )

    override fun delete(groupId: String, userId: String, commentId: String) {}
}
