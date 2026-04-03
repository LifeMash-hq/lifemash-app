package org.bmsk.lifemash.feed.data.repository

import org.bmsk.lifemash.feed.data.api.FeedApi
import org.bmsk.lifemash.feed.domain.model.FeedComment
import org.bmsk.lifemash.feed.domain.model.FeedFilter
import org.bmsk.lifemash.feed.domain.repository.FeedPage
import org.bmsk.lifemash.feed.domain.repository.FeedRepository

internal class FeedRepositoryImpl(private val api: FeedApi) : FeedRepository {
    override suspend fun getFeed(filter: FeedFilter, cursor: String?, limit: Int): FeedPage =
        api.getFeed(filter, cursor, limit)

    override suspend fun toggleLike(postId: String, isCurrentlyLiked: Boolean): Boolean {
        if (isCurrentlyLiked) api.unlike(postId) else api.like(postId)
        return !isCurrentlyLiked
    }

    override suspend fun getComments(postId: String): List<FeedComment> =
        api.getComments(postId)

    override suspend fun createComment(postId: String, content: String): FeedComment =
        api.createComment(postId, content)
}
