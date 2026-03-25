package org.bmsk.lifemash.event

import kotlinx.datetime.Instant
import org.bmsk.lifemash.model.calendar.CreateEventRequest
import org.bmsk.lifemash.model.calendar.EventDto
import org.bmsk.lifemash.model.calendar.UpdateEventRequest
import java.util.*

class StubEventRepository : EventRepository {
    private val epoch = Instant.fromEpochSeconds(0)
    private val events = mutableMapOf<UUID, EventDto>()

    override fun getMonthEvents(groupId: UUID, year: Int, month: Int): List<EventDto> =
        events.values.filter { it.groupId == groupId.toString() }

    override fun create(groupId: UUID, authorId: UUID, request: CreateEventRequest): EventDto {
        val id = UUID.randomUUID()
        val event = EventDto(
            id = id.toString(),
            groupId = groupId.toString(),
            authorId = authorId.toString(),
            title = request.title,
            description = request.description,
            startAt = request.startAt,
            endAt = request.endAt,
            isAllDay = request.isAllDay,
            color = request.color,
            createdAt = epoch,
            updatedAt = epoch,
        )
        events[id] = event
        return event
    }

    override fun update(eventId: UUID, request: UpdateEventRequest): EventDto {
        val existing = events[eventId] ?: EventDto(
            id = eventId.toString(),
            groupId = "demo",
            authorId = "demo",
            title = request.title ?: "Updated Event",
            startAt = request.startAt ?: epoch,
            createdAt = epoch,
            updatedAt = epoch,
        )
        val updated = existing.copy(
            title = request.title ?: existing.title,
            description = request.description ?: existing.description,
            startAt = request.startAt ?: existing.startAt,
            endAt = request.endAt ?: existing.endAt,
            isAllDay = request.isAllDay ?: existing.isAllDay,
            color = request.color ?: existing.color,
        )
        events[eventId] = updated
        return updated
    }

    override fun delete(eventId: UUID) {
        events.remove(eventId)
    }

    override fun findById(eventId: UUID): EventDto? =
        events[eventId]
}
