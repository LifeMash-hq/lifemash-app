package org.bmsk.lifemash.domain.calendar

import kotlin.time.Instant

interface EventRepository {
    suspend fun getMonthEvents(
        groupId: String,
        year: Int,
        month: Int,
    ): List<Event>
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
