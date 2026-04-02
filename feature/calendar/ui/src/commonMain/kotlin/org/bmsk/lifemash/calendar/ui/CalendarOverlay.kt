package org.bmsk.lifemash.calendar.ui

import org.bmsk.lifemash.calendar.domain.model.Event

internal sealed interface CalendarOverlay {
    data object None : CalendarOverlay
    data class EventDetail(val event: Event) : CalendarOverlay
    data object GroupRename : CalendarOverlay
}
