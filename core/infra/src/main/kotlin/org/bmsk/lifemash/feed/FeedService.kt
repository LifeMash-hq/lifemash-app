package org.bmsk.lifemash.feed

import org.bmsk.lifemash.model.feed.FeedPostDto
import org.bmsk.lifemash.model.feed.FeedResponse
import kotlin.uuid.Uuid

class FeedService(
    private val feedRepository: FeedRepository,
) {
    fun getFeed(userId: Uuid, cursor: String?, limit: Int = 20): FeedResponse {
        return feedRepository.getFeed(userId, cursor, limit)
    }

    fun getTrending(limit: Int = 20): List<FeedPostDto> {
        return feedRepository.getTrending(limit)
    }
}
