package org.bmsk.lifemash.explore

import org.bmsk.lifemash.model.explore.EventSummaryDto
import org.bmsk.lifemash.model.explore.HeatmapDayDto
import org.bmsk.lifemash.model.explore.PublicEventDto
import org.bmsk.lifemash.model.explore.UserSuggestionDto
import org.bmsk.lifemash.model.feed.FeedPostDto
import org.bmsk.lifemash.model.follow.UserSummaryDto
import org.bmsk.lifemash.feed.FeedRepository
import kotlin.uuid.Uuid

class ExploreService(
    private val exploreRepository: ExploreRepository,
    private val feedRepository: FeedRepository,
) {
    fun searchUsers(query: String): List<UserSummaryDto> = exploreRepository.searchUsers(query)
    fun searchEvents(query: String): List<EventSummaryDto> = exploreRepository.searchEvents(query)
    fun getTrending(limit: Int = 20): List<FeedPostDto> = feedRepository.getTrending(limit)
    fun getPublicEvents(category: String?, cursor: String?, limit: Int = 20): List<PublicEventDto> =
        exploreRepository.getPublicEvents(category, cursor, limit)
    fun getHeatmap(userId: Uuid, year: Int, month: Int): List<HeatmapDayDto> =
        exploreRepository.getHeatmap(userId, year, month)
    fun getFollowSuggestions(userId: Uuid, limit: Int = 10): List<UserSuggestionDto> =
        exploreRepository.getFollowSuggestions(userId, limit)
}
