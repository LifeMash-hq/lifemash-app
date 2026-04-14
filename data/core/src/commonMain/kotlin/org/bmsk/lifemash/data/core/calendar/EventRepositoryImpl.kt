package org.bmsk.lifemash.data.core.calendar

import kotlin.time.Instant
import org.bmsk.lifemash.domain.calendar.Event
import org.bmsk.lifemash.domain.calendar.EventRepository
import org.bmsk.lifemash.domain.calendar.EventVisibility
import org.bmsk.lifemash.data.remote.calendar.CalendarApi
import org.bmsk.lifemash.data.remote.calendar.dto.CreateEventRequest
import org.bmsk.lifemash.data.remote.calendar.dto.UpdateEventRequest

private fun EventVisibility.toApiType(): String = when (this) {
    is EventVisibility.Public -> "public"
    is EventVisibility.Followers -> "followers"
    is EventVisibility.Group -> "group"
    is EventVisibility.Specific -> "specific"
    is EventVisibility.Private -> "private"
}

internal class EventRepositoryImpl(private val api: CalendarApi) : EventRepository {

    override suspend fun getMonthEvents(
        groupId: String,
        year: Int,
        month: Int,
    ): List<Event> =
        api.getMonthEvents(
            groupId,
            year,
            month,
        ).map { it.toDomain() }

    override suspend fun createEvent(
        groupId: String,
        title: String,
        description: String?,
        location: String?,
        startAt: Instant,
        endAt: Instant?,
        isAllDay: Boolean,
        color: String?,
        visibility: EventVisibility,
    ): Event = api.createEvent(
        groupId,
        CreateEventRequest(
            title = title,
            description = description,
            location = location,
            startAt = startAt,
            endAt = endAt,
            isAllDay = isAllDay,
            color = color,
            visibility = visibility.toApiType(),
            visibilityGroupId = (visibility as? EventVisibility.Group)?.groupId,
            visibilityUserIds = (visibility as? EventVisibility.Specific)?.userIds,
        ),
    ).toDomain()

    override suspend fun updateEvent(
        groupId: String,
        eventId: String,
        title: String?,
        description: String?,
        location: String?,
        startAt: Instant?,
        endAt: Instant?,
        isAllDay: Boolean?,
        color: String?,
        visibility: EventVisibility?,
    ): Event = api.updateEvent(
        groupId,
        eventId,
        UpdateEventRequest(
            title = title,
            description = description,
            location = location,
            startAt = startAt,
            endAt = endAt,
            isAllDay = isAllDay,
            color = color,
            visibility = visibility?.toApiType(),
            visibilityGroupId = (visibility as? EventVisibility.Group)?.groupId,
            visibilityUserIds = (visibility as? EventVisibility.Specific)?.userIds,
        ),
    ).toDomain()

    override suspend fun deleteEvent(groupId: String, eventId: String) =
        api.deleteEvent(groupId, eventId)
}
