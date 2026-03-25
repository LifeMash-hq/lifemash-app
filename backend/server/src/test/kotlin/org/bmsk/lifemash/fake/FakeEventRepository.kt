package org.bmsk.lifemash.fake

import kotlin.time.Clock
import org.bmsk.lifemash.event.*
import org.bmsk.lifemash.model.calendar.CreateEventRequest
import org.bmsk.lifemash.model.calendar.EventDto
import org.bmsk.lifemash.model.calendar.UpdateEventRequest
import java.util.*

class FakeEventRepository : EventRepository {
    private val events = mutableMapOf<UUID, EventDto>()

    override fun getMonthEvents(groupId: UUID, year: Int, month: Int): List<EventDto> {
        return events.values.filter {
            it.groupId == groupId.toString() &&
                it.startAt.toString().startsWith("$year-${month.toString().padStart(2, '0')}")
        }.sortedBy { it.startAt }
    }

    override fun create(groupId: UUID, authorId: UUID, request: CreateEventRequest): EventDto {
        val id = UUID.randomUUID()
        val now = Clock.System.now()
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
            createdAt = now,
            updatedAt = now,
        )
        events[id] = event
        return event
    }

    override fun update(eventId: UUID, request: UpdateEventRequest): EventDto {
        val existing = events[eventId]!!
        val updated = existing.copy(
            title = request.title ?: existing.title,
            description = request.description ?: existing.description,
            startAt = request.startAt ?: existing.startAt,
            endAt = request.endAt ?: existing.endAt,
            isAllDay = request.isAllDay ?: existing.isAllDay,
            color = request.color ?: existing.color,
            updatedAt = Clock.System.now(),
        )
        events[eventId] = updated
        return updated
    }

    override fun delete(eventId: UUID) {
        events.remove(eventId)
    }

    override fun findById(eventId: UUID): EventDto? = events[eventId]
}
