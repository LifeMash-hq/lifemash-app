package org.bmsk.lifemash.calendar.api

import kotlinx.serialization.Serializable

@Serializable
data object CalendarRoute

@Serializable
data class EventCreateRoute(
    val year: Int,
    val month: Int,
    val day: Int = 0,
    val groupId: String? = null,
)

@Serializable
data class EventEditRoute(
    val groupId: String,
    val eventId: String,
    val eventTitle: String,
    val eventDescription: String? = null,
    val eventColor: String? = null,
    val eventIsAllDay: Boolean = false,
    val eventStartAt: Long = 0L,
    val eventEndAt: Long? = null,
)

const val CALENDAR_ROUTE = "calendar"
