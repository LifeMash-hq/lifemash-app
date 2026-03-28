package org.bmsk.lifemash.feed.domain.repository

import kotlinx.coroutines.flow.Flow
import org.bmsk.lifemash.feed.domain.model.FeedPost

interface FeedRepository {
    fun getFeed(cursor: String?, limit: Int): Flow<FeedPage>
    suspend fun toggleLike(postId: String): Boolean
}

data class FeedPage(
    val items: List<FeedPost>,
    val nextCursor: String? = null,
)
