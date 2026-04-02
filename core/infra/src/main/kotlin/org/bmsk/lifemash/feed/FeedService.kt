package org.bmsk.lifemash.feed

import org.bmsk.lifemash.model.feed.FeedPostDto
import org.bmsk.lifemash.model.feed.FeedResponse
import kotlin.uuid.Uuid

class FeedService(
    private val feedRepository: FeedRepository,
) {
    fun getFeed(userId: Uuid, cursor: String?, limit: Int = 20, filter: String = "following"): FeedResponse {
        return when (filter) {
            "all" -> feedRepository.getAllFeed(cursor, limit)
            "recommended" -> FeedResponse(items = feedRepository.getTrending(limit), nextCursor = null)
            else -> feedRepository.getFeed(userId, cursor, limit)
        }
    }

    fun getTrending(limit: Int = 20): List<FeedPostDto> {
        return feedRepository.getTrending(limit)
    }
}
