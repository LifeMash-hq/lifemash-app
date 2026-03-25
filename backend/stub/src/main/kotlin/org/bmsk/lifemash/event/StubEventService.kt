package org.bmsk.lifemash.event

import kotlinx.datetime.Instant
import org.bmsk.lifemash.model.calendar.CreateEventRequest
import org.bmsk.lifemash.model.calendar.EventDto
import org.bmsk.lifemash.model.calendar.UpdateEventRequest

class StubEventService : EventService {
    private val epoch = Instant.fromEpochSeconds(0)

    private fun demoEvent(groupId: String, userId: String) = EventDto(
        id = "demo-event-1",
        groupId = groupId,
        authorId = userId,
        title = "Demo Event",
        description = "This is a demo event",
        startAt = epoch,
        endAt = null,
        isAllDay = false,
        color = null,
        createdAt = epoch,
        updatedAt = epoch,
    )

    override fun getMonthEvents(groupId: String, userId: String, year: Int, month: Int): List<EventDto> =
        listOf(demoEvent(groupId, userId))

    override fun create(groupId: String, userId: String, request: CreateEventRequest): EventDto =
        demoEvent(groupId, userId)

    override fun update(groupId: String, userId: String, eventId: String, request: UpdateEventRequest): EventDto =
        demoEvent(groupId, userId)

    override fun delete(groupId: String, userId: String, eventId: String) {}
}
