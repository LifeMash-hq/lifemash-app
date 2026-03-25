package org.bmsk.lifemash.model.calendar

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class CommentDto(
    val id: String,
    val eventId: String,
    val authorId: String,
    val content: String,
    val createdAt: Instant,
)

@Serializable
data class CreateCommentRequest(val content: String)
