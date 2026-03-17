package org.bmsk.lifemash.calendar.domain.usecase

import org.bmsk.lifemash.calendar.domain.repository.EventRepository

interface DeleteEventUseCase {
    suspend operator fun invoke(groupId: String, eventId: String)
}

class DeleteEventUseCaseImpl(
    private val repository: EventRepository,
) : DeleteEventUseCase {
    override suspend operator fun invoke(groupId: String, eventId: String) =
        repository.deleteEvent(groupId, eventId)
}
