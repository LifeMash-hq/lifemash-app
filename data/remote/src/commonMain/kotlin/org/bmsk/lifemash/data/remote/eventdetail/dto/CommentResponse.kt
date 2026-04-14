package org.bmsk.lifemash.data.remote.eventdetail.dto

import kotlin.time.Instant
import kotlinx.serialization.Serializable

@Serializable
data class CommentResponse(
    val id: String,
    val eventId: String,
    val authorId: String,
    val authorNickname: String,
    val authorProfileImage: String? = null,
    val content: String,
    val createdAt: Instant,
)

@Serializable
data class CreateCommentRequest(val content: String)
