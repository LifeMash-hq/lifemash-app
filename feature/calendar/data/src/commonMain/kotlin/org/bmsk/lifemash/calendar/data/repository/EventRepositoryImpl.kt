package org.bmsk.lifemash.calendar.data.repository

import kotlin.time.Instant
import org.bmsk.lifemash.calendar.data.api.CalendarApi
import org.bmsk.lifemash.calendar.data.api.dto.CreateEventBody
import org.bmsk.lifemash.calendar.data.api.dto.UpdateEventBody
import org.bmsk.lifemash.calendar.domain.model.Event
import org.bmsk.lifemash.calendar.domain.model.EventVisibility
import org.bmsk.lifemash.calendar.domain.repository.EventRepository

private fun EventVisibility.toApiType(): String = when (this) {
    is EventVisibility.Public -> "public"
    is EventVisibility.Followers -> "followers"
    is EventVisibility.Group -> "group"
    is EventVisibility.Specific -> "specific"
    is EventVisibility.Private -> "private"
}

internal class EventRepositoryImpl(private val api: CalendarApi) : EventRepository {

    override suspend fun getMonthEvents(groupId: String, year: Int, month: Int): List<Event> =
        api.getMonthEvents(groupId, year, month).map { it.toDomain() }

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
        CreateEventBody(
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
        UpdateEventBody(
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
