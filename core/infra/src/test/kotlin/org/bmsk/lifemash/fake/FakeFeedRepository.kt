package org.bmsk.lifemash.fake

import org.bmsk.lifemash.model.feed.FeedPostDto
import org.bmsk.lifemash.feed.FeedRepository
import org.bmsk.lifemash.model.feed.FeedResponse
import kotlin.uuid.Uuid

class FakeFeedRepository : FeedRepository {
    val posts = mutableListOf<FeedPostDto>()

    override fun getFeed(userId: Uuid, cursor: String?, limit: Int): FeedResponse {
        val startIndex = if (cursor != null) cursor.toInt() else 0
        val items = posts.drop(startIndex).take(limit)
        val nextCursor = if (startIndex + limit < posts.size) (startIndex + limit).toString() else null
        return FeedResponse(items = items, nextCursor = nextCursor)
    }

    override fun getAllFeed(cursor: String?, limit: Int): FeedResponse {
        val startIndex = if (cursor != null) cursor.toInt() else 0
        val items = posts.drop(startIndex).take(limit)
        val nextCursor = if (startIndex + limit < posts.size) (startIndex + limit).toString() else null
        return FeedResponse(items = items, nextCursor = nextCursor)
    }

    override fun getTrending(limit: Int): List<FeedPostDto> {
        return posts.take(limit)
    }
}
