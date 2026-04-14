package org.bmsk.lifemash.domain.feed

interface FeedRepository {
    suspend fun getFeed(
        filter: FeedFilter,
        cursor: String?,
        limit: Int,
    ): FeedPage
    suspend fun toggleLike(postId: String, isCurrentlyLiked: Boolean): Boolean
    suspend fun getComments(postId: String): List<FeedComment>
    suspend fun createComment(postId: String, content: String): FeedComment
}

data class FeedPage(
    val items: List<FeedPost>,
    val nextCursor: String? = null,
)
