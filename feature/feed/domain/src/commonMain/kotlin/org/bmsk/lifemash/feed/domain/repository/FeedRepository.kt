package org.bmsk.lifemash.feed.domain.repository

import org.bmsk.lifemash.feed.domain.model.FeedFilter
import org.bmsk.lifemash.feed.domain.model.FeedPost

interface FeedRepository {
    suspend fun getFeed(filter: FeedFilter, cursor: String?, limit: Int): FeedPage
    suspend fun toggleLike(postId: String): Boolean
}

data class FeedPage(
    val items: List<FeedPost>,
    val nextCursor: String? = null,
)
