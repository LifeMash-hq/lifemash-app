package org.bmsk.lifemash.calendar.domain.repository

import kotlin.time.Instant
import org.bmsk.lifemash.calendar.domain.model.Event
import org.bmsk.lifemash.calendar.domain.model.EventVisibility

interface EventRepository {
    suspend fun getMonthEvents(groupId: String, year: Int, month: Int): List<Event>
    suspend fun createEvent(
        groupId: String,
        title: String,
        description: String?,
        location: String?,
        startAt: Instant,
        endAt: Instant?,
        isAllDay: Boolean,
        color: String?,
        visibility: EventVisibility = EventVisibility.Followers,
    ): Event
    suspend fun updateEvent(
        groupId: String,
        eventId: String,
        title: String?,
        description: String?,
        location: String?,
        startAt: Instant?,
        endAt: Instant?,
        isAllDay: Boolean?,
        color: String?,
        visibility: EventVisibility? = null,
    ): Event
    suspend fun deleteEvent(groupId: String, eventId: String)
}
