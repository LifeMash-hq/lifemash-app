package org.bmsk.lifemash.calendar.impl

import org.bmsk.lifemash.domain.calendar.Event

internal sealed interface CalendarOverlay {
    data object None : CalendarOverlay
    data class EventDetail(val event: Event) : CalendarOverlay
    data object GroupRename : CalendarOverlay
}