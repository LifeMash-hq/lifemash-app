package org.bmsk.lifemash.explore

import org.bmsk.lifemash.model.explore.EventSummaryDto
import org.bmsk.lifemash.model.explore.HeatmapDayDto
import org.bmsk.lifemash.model.explore.PublicEventDto
import org.bmsk.lifemash.model.explore.UserSuggestionDto
import org.bmsk.lifemash.model.follow.UserSummaryDto
import kotlin.uuid.Uuid

interface ExploreRepository {
    fun searchUsers(query: String): List<UserSummaryDto>
    fun searchEvents(query: String): List<EventSummaryDto>
    fun getPublicEvents(category: String?, cursor: String?, limit: Int): List<PublicEventDto>
    fun getHeatmap(userId: Uuid, year: Int, month: Int): List<HeatmapDayDto>
    fun getFollowSuggestions(userId: Uuid, limit: Int): List<UserSuggestionDto>
}
