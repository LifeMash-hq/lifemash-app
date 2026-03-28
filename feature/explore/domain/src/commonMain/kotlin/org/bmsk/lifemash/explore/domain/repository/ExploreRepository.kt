package org.bmsk.lifemash.explore.domain.repository

import kotlinx.coroutines.flow.Flow
import org.bmsk.lifemash.explore.domain.model.EventSummary
import org.bmsk.lifemash.explore.domain.model.ExploreMoment
import org.bmsk.lifemash.explore.domain.model.UserSummary

interface ExploreRepository {
    fun searchUsers(query: String): Flow<List<UserSummary>>
    fun searchEvents(query: String): Flow<List<EventSummary>>
    fun getTrending(): Flow<List<UserSummary>>
    fun getTrendingMoments(): Flow<List<ExploreMoment>>
}
