@file:OptIn(kotlin.uuid.ExperimentalUuidApi::class, kotlin.time.ExperimentalTime::class)
package org.bmsk.lifemash.fake

import kotlin.time.Clock
import kotlin.uuid.Uuid
import org.bmsk.lifemash.event.EventRepository
import org.bmsk.lifemash.model.calendar.CreateEventRequest
import org.bmsk.lifemash.model.calendar.EventDetailDto
import org.bmsk.lifemash.model.calendar.EventDto
import org.bmsk.lifemash.model.calendar.UpdateEventRequest

class FakeEventRepository : EventRepository {
    private val events = mutableMapOf<Uuid, EventDto>()
    private val eventDetails = mutableMapOf<Uuid, EventDetailDto>()
    private val attendees = mutableSetOf<Pair<Uuid, Uuid>>() // eventId to userId

    fun addEvent(event: EventDto) {
        events[Uuid.parse(event.id)] = event
    }

    fun setEventDetail(eventId: Uuid, detail: EventDetailDto) {
        eventDetails[eventId] = detail
    }

    override fun getMonthEvents(groupId: Uuid, year: Int, month: Int): List<EventDto> {
        return events.values.filter { it.groupId == groupId.toString() }
    }

    override fun create(groupId: Uuid, authorId: Uuid, request: CreateEventRequest): EventDto {
        val now = Clock.System.now()
        val event = EventDto(
            id = Uuid.random().toString(),
            groupId = groupId.toString(),
            authorId = authorId.toString(),
            title = request.title,
            description = request.description,
            startAt = request.startAt,
            endAt = request.endAt,
            isAllDay = request.isAllDay,
            createdAt = now,
            updatedAt = now,
        )
        events[Uuid.parse(event.id)] = event
        return event
    }

    override fun update(eventId: Uuid, request: UpdateEventRequest): EventDto {
        val existing = events[eventId] ?: throw RuntimeException("Not found")
        val updated = existing.copy(
            title = request.title ?: existing.title,
            description = request.description ?: existing.description,
            startAt = request.startAt ?: existing.startAt,
            endAt = request.endAt ?: existing.endAt,
            isAllDay = request.isAllDay ?: existing.isAllDay,
            location = request.location ?: existing.location,
            imageEmoji = request.imageEmoji ?: existing.imageEmoji
        )
        events[eventId] = updated
        return updated
    }

    override fun delete(eventId: Uuid) {
        events.remove(eventId)
    }

    override fun findById(eventId: Uuid): EventDto? = events[eventId]

    override fun getEventDetail(eventId: Uuid, viewerId: Uuid): EventDetailDto? {
        return eventDetails[eventId]?.copy(isJoined = (eventId to viewerId) in attendees)
    }

    override fun toggleJoin(eventId: Uuid, userId: Uuid): Boolean {
        val pair = eventId to userId
        return if (pair in attendees) {
            attendees.remove(pair)
            false
        } else {
            attendees.add(pair)
            true
        }
    }
}
