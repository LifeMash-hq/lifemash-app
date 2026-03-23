package org.bmsk.lifemash.calendar.ui

internal data class EventFormData(
    val title: String,
    val description: String?,
    val startAt: Long,
    val endAt: Long?,
    val isAllDay: Boolean,
    val color: String?,
)
