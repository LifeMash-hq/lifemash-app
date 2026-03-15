package org.bmsk.lifemash.calendar.data.api.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import org.bmsk.lifemash.calendar.domain.model.Comment

@Serializable
data class CommentDto(
    val id: String,
    val eventId: String,
    val authorId: String,
    val content: String,
    val createdAt: Instant,
) {
    fun toDomain() = Comment(
        id = id,
        eventId = eventId,
        authorId = authorId,
        content = content,
        createdAt = createdAt,
    )
}

@Serializable
data class CreateCommentBody(val content: String)
