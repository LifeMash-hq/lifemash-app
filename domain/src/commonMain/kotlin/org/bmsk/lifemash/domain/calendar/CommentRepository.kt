package org.bmsk.lifemash.domain.calendar

interface CommentRepository {
    suspend fun getComments(groupId: String, eventId: String): List<Comment>
    suspend fun createComment(
        groupId: String,
        eventId: String,
        content: String,
    ): Comment
    suspend fun deleteComment(
        groupId: String,
        eventId: String,
        commentId: String,
    )
}
