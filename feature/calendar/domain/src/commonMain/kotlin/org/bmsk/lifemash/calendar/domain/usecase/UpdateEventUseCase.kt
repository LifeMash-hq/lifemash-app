package org.bmsk.lifemash.calendar.domain.usecase

import org.bmsk.lifemash.calendar.domain.model.Event
import org.bmsk.lifemash.calendar.domain.repository.EventRepository
import org.bmsk.lifemash.calendar.domain.repository.UpdateEventRequest

class UpdateEventUseCase(private val repository: EventRepository) {
    suspend operator fun invoke(groupId: String, eventId: String, request: UpdateEventRequest): Event =
        repository.updateEvent(groupId, eventId, request)
}
