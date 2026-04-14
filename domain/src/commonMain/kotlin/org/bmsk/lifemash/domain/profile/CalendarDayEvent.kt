package org.bmsk.lifemash.domain.profile

data class CalendarDayEvent(
    val id: String,
    val title: String,
    val color: String,
    val visibility: String = "public",
)
