package org.bmsk.lifemash.profile.domain.model

data class CalendarDayEvent(
    val id: String,
    val title: String,
    val color: String,
    val visibility: String = "public",
)
