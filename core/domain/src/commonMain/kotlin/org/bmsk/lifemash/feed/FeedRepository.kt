package org.bmsk.lifemash.feed

import org.bmsk.lifemash.model.feed.FeedPostDto
import org.bmsk.lifemash.model.feed.FeedResponse
import kotlin.uuid.Uuid

interface FeedRepository {
    fun getFeed(userId: Uuid, cursor: String?, limit: Int): FeedResponse
    fun getAllFeed(cursor: String?, limit: Int): FeedResponse
    fun getTrending(limit: Int): List<FeedPostDto>
}
