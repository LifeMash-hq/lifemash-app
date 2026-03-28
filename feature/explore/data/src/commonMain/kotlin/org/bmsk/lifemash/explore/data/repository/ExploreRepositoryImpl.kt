package org.bmsk.lifemash.explore.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import org.bmsk.lifemash.explore.data.api.ExploreApi
import org.bmsk.lifemash.explore.domain.model.EventSummary
import org.bmsk.lifemash.explore.domain.model.ExploreMoment
import org.bmsk.lifemash.explore.domain.model.UserSummary
import org.bmsk.lifemash.explore.domain.repository.ExploreRepository

internal class ExploreRepositoryImpl(private val api: ExploreApi) : ExploreRepository {
    override fun searchUsers(query: String): Flow<List<UserSummary>> = flow { emit(api.searchUsers(query)) }
    override fun searchEvents(query: String): Flow<List<EventSummary>> = flow { emit(api.searchEvents(query)) }
    override fun getTrending(): Flow<List<UserSummary>> = flow { emit(api.getTrending()) }

    override fun getTrendingMoments(): Flow<List<ExploreMoment>> = flowOf(
        listOf(
            ExploreMoment("m1", "🍣", "청담 오마카세", "이수아"),
            ExploreMoment("m2", "🎉", "삼성전자 신입교육", "박현우"),
            ExploreMoment("m3", "✈️", "제주도 여행", "정재원"),
            ExploreMoment("m4", "💍", "결혼식", "김민지"),
            ExploreMoment("m5", "🏊", "수영 모임", "최준혁"),
            ExploreMoment("m6", "🎸", "밴드 공연", "한소희"),
        )
    )
}
