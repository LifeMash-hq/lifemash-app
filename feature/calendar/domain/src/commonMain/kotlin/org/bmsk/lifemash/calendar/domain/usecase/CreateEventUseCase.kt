package org.bmsk.lifemash.calendar.domain.usecase

import org.bmsk.lifemash.calendar.domain.model.Event
import org.bmsk.lifemash.calendar.domain.repository.CreateEventRequest
import org.bmsk.lifemash.calendar.domain.repository.EventRepository

interface CreateEventUseCase {
    suspend operator fun invoke(groupId: String, request: CreateEventRequest): Event
}

class CreateEventUseCaseImpl(
    private val repository: EventRepository,
) : CreateEventUseCase {
    override suspend operator fun invoke(groupId: String, request: CreateEventRequest): Event =
        repository.createEvent(groupId, request)
}
