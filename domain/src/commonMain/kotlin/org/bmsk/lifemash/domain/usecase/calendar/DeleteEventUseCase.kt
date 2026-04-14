package org.bmsk.lifemash.domain.usecase.calendar

import org.bmsk.lifemash.domain.calendar.EventRepository

class DeleteEventUseCase(private val repository: EventRepository) {
    suspend operator fun invoke(groupId: String, eventId: String) =
        repository.deleteEvent(groupId, eventId)
}
