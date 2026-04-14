package org.bmsk.lifemash.data.remote.feed.dto

import kotlinx.serialization.Serializable

@Serializable
data class FeedPostDto(
    val id: String,
    val authorId: String,
    val authorNickname: String,
    val authorProfileImage: String? = null,
    val eventId: String? = null,
    val eventTitle: String? = null,
    val eventDate: String? = null,
    val media: List<FeedMediaDto> = emptyList(),
    val caption: String? = null,
    val previewComments: List<FeedCommentDto> = emptyList(),
    val likeCount: Int = 0,
    val isLiked: Boolean = false,
    val commentCount: Int = 0,
    val createdAt: String,
)

@Serializable
data class FeedMediaDto(
    val mediaUrl: String,
    val mediaType: String,
    val sortOrder: Int,
    val width: Int? = null,
    val height: Int? = null,
    val durationMs: Long? = null,
)

@Serializable
data class FeedCommentDto(
    val id: String = "",
    val authorId: String = "",
    val authorNickname: String,
    val authorProfileImage: String? = null,
    val content: String,
    val createdAt: String = "",
)

@Serializable
data class FeedPageDto(
    val items: List<FeedPostDto>,
    val nextCursor: String? = null,
)
