package org.bmsk.lifemash.calendar.ui

import kotlinx.datetime.LocalDate
import org.bmsk.lifemash.calendar.domain.model.Event

internal sealed interface CalendarOverlay {
    data object None : CalendarOverlay
    data class EventCreate(val selectedDate: LocalDate?) : CalendarOverlay
    data class EventEdit(val event: Event) : CalendarOverlay
    data class EventDetail(val event: Event) : CalendarOverlay
    data object GroupRename : CalendarOverlay
}
