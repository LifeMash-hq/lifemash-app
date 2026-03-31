package org.bmsk.lifemash.eventdetail.data.repository

import org.bmsk.lifemash.eventdetail.domain.model.EventComment
import org.bmsk.lifemash.eventdetail.domain.model.EventDetail
import org.bmsk.lifemash.eventdetail.domain.repository.EventDetailRepository

// TODO: 백엔드 GET /events/{eventId} API 구현 후 실제 API 호출로 교체
internal class EventDetailRepositoryImpl : EventDetailRepository {

    override suspend fun getEventDetail(eventId: String): EventDetail {
        throw NotImplementedError("이벤트 상세 조회 API가 아직 구현되지 않았습니다")
    }

    override suspend fun toggleJoin(eventId: String): Boolean {
        throw NotImplementedError("참여 토글 API가 아직 구현되지 않았습니다")
    }

    override suspend fun addComment(eventId: String, content: String): EventComment {
        throw NotImplementedError("댓글 추가 API가 아직 구현되지 않았습니다")
    }
}
