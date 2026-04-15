package org.bmsk.lifemash.domain.calendar

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
        timing: EventTiming,
        color: String?,
        visibility: EventVisibility = EventVisibility.Followers,
    ): Event
    suspend fun updateEvent(
        groupId: String,
        eventId: String,
        title: String?,
        description: String?,
        location: String?,
        timing: EventTiming?,
        color: String?,
        visibility: EventVisibility? = null,
    ): Event
    suspend fun deleteEvent(groupId: String, eventId: String)
}
