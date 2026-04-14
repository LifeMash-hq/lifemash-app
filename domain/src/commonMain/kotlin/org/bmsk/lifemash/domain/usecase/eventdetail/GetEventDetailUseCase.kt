package org.bmsk.lifemash.domain.usecase.eventdetail

import org.bmsk.lifemash.domain.eventdetail.EventDetail
import org.bmsk.lifemash.domain.eventdetail.EventDetailRepository

class GetEventDetailUseCase(private val repository: EventDetailRepository) {
    suspend operator fun invoke(eventId: String): EventDetail = repository.getEventDetail(eventId)
}
