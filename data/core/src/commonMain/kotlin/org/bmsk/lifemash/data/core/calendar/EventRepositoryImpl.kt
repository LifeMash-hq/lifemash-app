package org.bmsk.lifemash.data.core.calendar

import org.bmsk.lifemash.domain.calendar.Event
import org.bmsk.lifemash.domain.calendar.EventRepository
import org.bmsk.lifemash.domain.calendar.EventTiming
import org.bmsk.lifemash.domain.calendar.EventVisibility
import org.bmsk.lifemash.data.remote.calendar.CalendarApi
import org.bmsk.lifemash.data.remote.calendar.dto.CreateEventRequest
import org.bmsk.lifemash.data.remote.calendar.dto.UpdateEventRequest

internal class EventRepositoryImpl(private val api: CalendarApi) : EventRepository {

    override suspend fun getMonthEvents(
        groupId: String,
        year: Int,
        month: Int,
    ): List<Event> =
        api.getMonthEvents(
            groupId = groupId,
            year = year,
            month = month,
        ).map { it.toDomain() }

    override suspend fun createEvent(
        groupId: String,
        title: String,
        description: String?,
        location: String?,
        timing: EventTiming,
        color: String?,
        visibility: EventVisibility,
    ): Event {
        val vis = visibility.toRequestFields()
        val time = timing.toRequestFields()
        return api.createEvent(
            groupId = groupId,
            body = CreateEventRequest(
                title = title,
                description = description,
                location = location,
                startAt = time.startAt,
                endAt = time.endAt,
                isAllDay = time.isAllDay,
                color = color,
                visibility = vis.type,
                visibilityGroupId = vis.groupId,
                visibilityUserIds = vis.userIds,
            ),
        ).toDomain()
    }

    override suspend fun updateEvent(
        groupId: String,
        eventId: String,
        title: String?,
        description: String?,
        location: String?,
        timing: EventTiming?,
        color: String?,
        visibility: EventVisibility?,
    ): Event {
        val vis = visibility?.toRequestFields()
        val time = timing?.toRequestFields()
        return api.updateEvent(
            groupId = groupId,
            eventId = eventId,
            body = UpdateEventRequest(
                title = title,
                description = description,
                location = location,
                startAt = time?.startAt,
                endAt = time?.endAt,
                isAllDay = time?.isAllDay,
                color = color,
                visibility = vis?.type,
                visibilityGroupId = vis?.groupId,
                visibilityUserIds = vis?.userIds,
            ),
        ).toDomain()
    }

    override suspend fun deleteEvent(groupId: String, eventId: String) {
        api.deleteEvent(groupId, eventId)
    }
}
