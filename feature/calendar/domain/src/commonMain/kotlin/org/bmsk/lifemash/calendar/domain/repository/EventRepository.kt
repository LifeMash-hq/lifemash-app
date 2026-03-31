package org.bmsk.lifemash.calendar.domain.repository

import kotlin.time.Instant
import org.bmsk.lifemash.calendar.domain.model.Event

interface EventRepository {
    suspend fun getMonthEvents(groupId: String, year: Int, month: Int): List<Event>
    suspend fun createEvent(
        groupId: String,
        title: String,
        description: String?,
        startAt: Instant,
        endAt: Instant?,
        isAllDay: Boolean,
        color: String?,
    ): Event
    suspend fun updateEvent(
        groupId: String,
        eventId: String,
        title: String?,
        description: String?,
        startAt: Instant?,
        endAt: Instant?,
        isAllDay: Boolean?,
        color: String?,
    ): Event
    suspend fun deleteEvent(groupId: String, eventId: String)
}
