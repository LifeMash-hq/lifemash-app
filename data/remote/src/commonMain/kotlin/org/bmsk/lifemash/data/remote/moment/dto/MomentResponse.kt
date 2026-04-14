package org.bmsk.lifemash.data.remote.moment.dto

import kotlinx.serialization.Serializable

@Serializable
data class MediaItemResponse(
    val mediaUrl: String,
    val mediaType: String,
    val sortOrder: Int,
    val width: Int? = null,
    val height: Int? = null,
    val durationMs: Long? = null,
)

@Serializable
data class CreateMomentRequest(
    val eventId: String? = null,
    val caption: String? = null,
    val visibility: String = "public",
    val media: List<MediaItemResponse> = emptyList(),
)

@Serializable
data class MomentResponse(
    val id: String,
    val eventId: String? = null,
    val eventTitle: String? = null,
    val authorId: String,
    val authorNickname: String,
    val authorProfileImage: String? = null,
    val caption: String? = null,
    val visibility: String,
    val media: List<MediaItemResponse>,
    val createdAt: String,
)
