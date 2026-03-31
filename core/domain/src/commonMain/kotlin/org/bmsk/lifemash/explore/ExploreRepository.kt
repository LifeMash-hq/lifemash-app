package org.bmsk.lifemash.explore

import org.bmsk.lifemash.model.explore.EventSummaryDto
import org.bmsk.lifemash.model.follow.UserSummaryDto

interface ExploreRepository {
    fun searchUsers(query: String): List<UserSummaryDto>
    fun searchEvents(query: String): List<EventSummaryDto>
}
