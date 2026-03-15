package org.bmsk.lifemash.calendar.domain.usecase

import org.bmsk.lifemash.calendar.domain.repository.EventRepository

class DeleteEventUseCase(private val repository: EventRepository) {
    suspend operator fun invoke(groupId: String, eventId: String) =
        repository.deleteEvent(groupId, eventId)
}
