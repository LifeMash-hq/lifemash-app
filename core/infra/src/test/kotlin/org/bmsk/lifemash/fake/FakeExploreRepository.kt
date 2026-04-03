package org.bmsk.lifemash.fake

import org.bmsk.lifemash.explore.ExploreRepository
import org.bmsk.lifemash.model.explore.EventSummaryDto
import org.bmsk.lifemash.model.explore.HeatmapDayDto
import org.bmsk.lifemash.model.explore.PublicEventDto
import org.bmsk.lifemash.model.explore.UserSuggestionDto
import org.bmsk.lifemash.model.follow.UserSummaryDto
import kotlin.uuid.Uuid

class FakeExploreRepository : ExploreRepository {
    val users = mutableListOf<UserSummaryDto>()
    val events = mutableListOf<EventSummaryDto>()

    override fun searchUsers(query: String): List<UserSummaryDto> =
        users.filter { it.nickname.contains(query, ignoreCase = true) }

    override fun searchEvents(query: String): List<EventSummaryDto> =
        events.filter { it.title.contains(query, ignoreCase = true) }

    override fun getPublicEvents(category: String?, cursor: String?, limit: Int): List<PublicEventDto> =
        emptyList()

    override fun getHeatmap(userId: Uuid, year: Int, month: Int): List<HeatmapDayDto> =
        emptyList()

    override fun getFollowSuggestions(userId: Uuid, limit: Int): List<UserSuggestionDto> =
        emptyList()
}
