package org.bmsk.lifemash.feed.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class FeedComment(
    val authorNickname: String,
    val content: String,
)

@Serializable
data class FeedPost(
    val id: String,
    val authorId: String,
    val authorNickname: String,
    val authorProfileImage: String? = null,
    val eventId: String,
    val eventTitle: String,
    val eventDate: String? = null,
    val imageUrl: String,
    val caption: String? = null,
    val previewComments: List<FeedComment> = emptyList(),
    val likeCount: Int = 0,
    val isLiked: Boolean = false,
    val commentCount: Int = 0,
    val createdAt: String,
)
