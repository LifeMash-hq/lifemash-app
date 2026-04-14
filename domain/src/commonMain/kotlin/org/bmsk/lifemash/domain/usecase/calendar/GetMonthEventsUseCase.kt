package org.bmsk.lifemash.domain.usecase.calendar

import org.bmsk.lifemash.domain.calendar.Event
import org.bmsk.lifemash.domain.calendar.EventRepository

class GetMonthEventsUseCase(private val repository: EventRepository) {
    suspend operator fun invoke(groupId: String, year: Int, month: Int): List<Event> =
        repository.getMonthEvents(groupId, year, month)
}
