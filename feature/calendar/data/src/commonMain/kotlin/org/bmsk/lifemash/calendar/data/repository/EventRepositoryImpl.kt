package org.bmsk.lifemash.calendar.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.bmsk.lifemash.calendar.data.api.CalendarApi
import org.bmsk.lifemash.calendar.data.api.dto.CreateEventBody
import org.bmsk.lifemash.calendar.data.api.dto.UpdateEventBody
import org.bmsk.lifemash.calendar.domain.model.Event
import org.bmsk.lifemash.calendar.domain.repository.CreateEventRequest
import org.bmsk.lifemash.calendar.domain.repository.EventRepository
import org.bmsk.lifemash.calendar.domain.repository.UpdateEventRequest

internal class EventRepositoryImpl(private val api: CalendarApi) : EventRepository {

    override fun getMonthEvents(groupId: String, year: Int, month: Int): Flow<List<Event>> = flow {
        emit(api.getMonthEvents(groupId, year, month).map { it.toDomain() })
    }

    override suspend fun createEvent(groupId: String, request: CreateEventRequest): Event =
        api.createEvent(
            groupId,
            CreateEventBody(
                title = request.title,
                description = request.description,
                startAt = request.startAt,
                endAt = request.endAt,
                isAllDay = request.isAllDay,
                color = request.color,
            ),
        ).toDomain()

    override suspend fun updateEvent(groupId: String, eventId: String, request: UpdateEventRequest): Event =
        api.updateEvent(
            groupId,
            eventId,
            UpdateEventBody(
                title = request.title,
                description = request.description,
                startAt = request.startAt,
                endAt = request.endAt,
                isAllDay = request.isAllDay,
                color = request.color,
            ),
        ).toDomain()

    override suspend fun deleteEvent(groupId: String, eventId: String) =
        api.deleteEvent(groupId, eventId)
}
