package org.bmsk.lifemash.calendar.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.bmsk.lifemash.calendar.domain.model.Event
import org.bmsk.lifemash.calendar.domain.repository.EventRepository

class GetMonthEventsUseCase(private val repository: EventRepository) {
    operator fun invoke(groupId: String, year: Int, month: Int): Flow<List<Event>> =
        repository.getMonthEvents(groupId, year, month)
}
