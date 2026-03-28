package org.bmsk.lifemash.feed.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.bmsk.lifemash.feed.data.api.FeedApi
import org.bmsk.lifemash.feed.domain.repository.FeedPage
import org.bmsk.lifemash.feed.domain.repository.FeedRepository

internal class FeedRepositoryImpl(private val api: FeedApi) : FeedRepository {
    override fun getFeed(cursor: String?, limit: Int): Flow<FeedPage> = flow {
        emit(api.getFeed(cursor, limit))
    }
    override suspend fun toggleLike(postId: String): Boolean {
        api.like(postId)
        return true
    }
}
