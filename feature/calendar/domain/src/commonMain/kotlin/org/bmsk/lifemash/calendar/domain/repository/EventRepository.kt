package org.bmsk.lifemash.calendar.domain.repository

import kotlinx.coroutines.flow.Flow
import org.bmsk.lifemash.calendar.domain.model.Event

interface EventRepository {
    fun getMonthEvents(groupId: String, year: Int, month: Int): Flow<List<Event>>
    suspend fun createEvent(groupId: String, request: CreateEventRequest): Event
    suspend fun updateEvent(groupId: String, eventId: String, request: UpdateEventRequest): Event
    suspend fun deleteEvent(groupId: String, eventId: String)
}

data class CreateEventRequest(
    val title: String,
    val description: String?,
    val startAt: kotlinx.datetime.Instant,
    val endAt: kotlinx.datetime.Instant?,
    val isAllDay: Boolean,
    val color: String?,
)

data class UpdateEventRequest(
    val title: String?,
    val description: String?,
    val startAt: kotlinx.datetime.Instant?,
    val endAt: kotlinx.datetime.Instant?,
    val isAllDay: Boolean?,
    val color: String?,
)
