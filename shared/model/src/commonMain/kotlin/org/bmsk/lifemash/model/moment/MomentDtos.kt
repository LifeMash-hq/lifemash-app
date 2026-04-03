package org.bmsk.lifemash.model.moment

import kotlinx.serialization.Serializable

@Serializable
data class MediaItemDto(
    val mediaUrl: String,
    val mediaType: String,          // "image" | "video"
    val sortOrder: Int,
    val width: Int? = null,
    val height: Int? = null,
    val durationMs: Long? = null,   // 동영상 전용 (ms)
)

@Serializable
data class CreateMomentRequest(
    val eventId: String? = null,
    val caption: String? = null,
    val visibility: String = "public",  // "public" | "followers" | "private"
    val media: List<MediaItemDto> = emptyList(),
)

@Serializable
data class UpdateMomentRequest(
    val caption: String? = null,
    val visibility: String? = null,
    val media: List<MediaItemDto>? = null,  // null = 변경 없음
)

@Serializable
data class MomentDto(
    val id: String,
    val eventId: String? = null,
    val eventTitle: String? = null,
    val authorId: String,
    val authorNickname: String,
    val authorProfileImage: String? = null,
    val caption: String? = null,
    val visibility: String,
    val media: List<MediaItemDto>,
    val createdAt: String,
)
