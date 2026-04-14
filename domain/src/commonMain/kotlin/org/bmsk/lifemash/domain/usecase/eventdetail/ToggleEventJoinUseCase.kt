package org.bmsk.lifemash.domain.usecase.eventdetail

import org.bmsk.lifemash.domain.eventdetail.EventDetailRepository

class ToggleEventJoinUseCase(private val repository: EventDetailRepository) {
    suspend operator fun invoke(eventId: String): Boolean = repository.toggleJoin(eventId)
}
