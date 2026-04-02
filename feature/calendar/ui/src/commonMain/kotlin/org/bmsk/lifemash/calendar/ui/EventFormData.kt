package org.bmsk.lifemash.calendar.ui

import org.bmsk.lifemash.calendar.domain.model.EventVisibility

internal data class EventFormData(
    val title: String,
    val description: String?,
    val location: String?,
    val startAt: Long,
    val endAt: Long?,
    val isAllDay: Boolean,
    val color: String?,
    val visibility: EventVisibility = EventVisibility.Followers,
)
