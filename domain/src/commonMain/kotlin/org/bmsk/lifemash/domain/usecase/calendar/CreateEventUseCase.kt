package org.bmsk.lifemash.domain.usecase.calendar

import kotlin.time.Instant
import org.bmsk.lifemash.domain.calendar.Event
import org.bmsk.lifemash.domain.calendar.EventRepository
import org.bmsk.lifemash.domain.calendar.EventVisibility

class CreateEventUseCase(private val repository: EventRepository) {
    suspend operator fun invoke(
        groupId: String,
        title: String,
        description: String?,
        location: String?,
        startAt: Instant,
        endAt: Instant?,
        isAllDay: Boolean,
        color: String?,
        visibility: EventVisibility = EventVisibility.Followers,
    ): Event = repository.createEvent(
        groupId, title, description, location, startAt, endAt, isAllDay, color, visibility,
    )
}
