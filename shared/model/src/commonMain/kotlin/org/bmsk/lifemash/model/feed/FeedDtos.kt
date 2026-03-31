package org.bmsk.lifemash.model.feed

import kotlinx.serialization.Serializable

@Serializable
data class FeedPostDto(
    val id: String,
    val authorId: String,
    val authorNickname: String,
    val authorProfileImage: String? = null,
    val eventId: String,
    val eventTitle: String,
    val imageUrl: String,
    val caption: String? = null,
    val likeCount: Int = 0,
    val isLiked: Boolean = false,
    val commentPreview: List<FeedCommentDto> = emptyList(),
    val commentCount: Int = 0,
    val createdAt: String,
)

@Serializable
data class FeedCommentDto(
    val authorNickname: String,
    val content: String,
)

@Serializable
data class FeedResponse(
    val items: List<FeedPostDto>,
    val nextCursor: String? = null,
)
