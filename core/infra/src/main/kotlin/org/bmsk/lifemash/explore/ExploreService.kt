package org.bmsk.lifemash.explore

import org.bmsk.lifemash.model.explore.EventSummaryDto
import org.bmsk.lifemash.model.feed.FeedPostDto
import org.bmsk.lifemash.model.follow.UserSummaryDto
import org.bmsk.lifemash.feed.FeedRepository

class ExploreService(
    private val exploreRepository: ExploreRepository,
    private val feedRepository: FeedRepository,
) {
    fun searchUsers(query: String): List<UserSummaryDto> = exploreRepository.searchUsers(query)
    fun searchEvents(query: String): List<EventSummaryDto> = exploreRepository.searchEvents(query)
    fun getTrending(limit: Int = 20): List<FeedPostDto> = feedRepository.getTrending(limit)
}
