package org.bmsk.lifemash.fake

import org.bmsk.lifemash.model.explore.EventSummaryDto
import org.bmsk.lifemash.explore.ExploreRepository
import org.bmsk.lifemash.model.follow.UserSummaryDto

class FakeExploreRepository : ExploreRepository {
    val users = mutableListOf<UserSummaryDto>()
    val events = mutableListOf<EventSummaryDto>()

    override fun searchUsers(query: String): List<UserSummaryDto> {
        return users.filter { it.nickname.contains(query, ignoreCase = true) }
    }

    override fun searchEvents(query: String): List<EventSummaryDto> {
        return events.filter { it.title.contains(query, ignoreCase = true) }
    }
}
