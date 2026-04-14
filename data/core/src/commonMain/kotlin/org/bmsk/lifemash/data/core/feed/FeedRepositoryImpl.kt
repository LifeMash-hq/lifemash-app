package org.bmsk.lifemash.data.core.feed

import org.bmsk.lifemash.domain.feed.FeedComment
import org.bmsk.lifemash.domain.feed.FeedFilter
import org.bmsk.lifemash.domain.feed.FeedPage
import org.bmsk.lifemash.domain.feed.FeedRepository
import org.bmsk.lifemash.data.remote.feed.FeedApi

internal class FeedRepositoryImpl(private val api: FeedApi) : FeedRepository {
    override suspend fun getFeed(
        filter: FeedFilter,
        cursor: String?,
        limit: Int,
    ): FeedPage =
        api.getFeed(filter.queryValue, cursor, limit).toDomain()

    override suspend fun toggleLike(postId: String, isCurrentlyLiked: Boolean): Boolean {
        if (isCurrentlyLiked) api.unlike(postId) else api.like(postId)
        return !isCurrentlyLiked
    }

    override suspend fun getComments(postId: String): List<FeedComment> =
        api.getComments(postId).map { it.toDomain() }

    override suspend fun createComment(postId: String, content: String): FeedComment =
        api.createComment(postId, content).toDomain()
}
