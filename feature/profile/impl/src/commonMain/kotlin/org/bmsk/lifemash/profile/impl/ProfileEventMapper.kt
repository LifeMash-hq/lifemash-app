package org.bmsk.lifemash.profile.impl

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.bmsk.lifemash.domain.calendar.Event
import org.bmsk.lifemash.domain.calendar.EventTiming
import org.bmsk.lifemash.domain.profile.CalendarDayEvent
import org.bmsk.lifemash.domain.profile.ProfileEvent

private const val DEFAULT_EVENT_COLOR = "#4F6AF5"

internal fun Event.toCalendarDayEvent(): CalendarDayEvent = CalendarDayEvent(
    id = id,
    title = title,
    color = color ?: DEFAULT_EVENT_COLOR,
)

internal fun Event.toProfileEvent(): ProfileEvent {
    val tz = TimeZone.currentSystemDefault()
    val (startLabel, endLabel) = when (val t = timing) {
        is EventTiming.AllDay -> "" to ""
        is EventTiming.Timed -> {
            t.start.toLocalDateTime(tz).formatTime() to t.end.toLocalDateTime(tz).formatTime()
        }
    }
    return ProfileEvent(
        id = id,
        title = title,
        startTime = startLabel,
        endTime = endLabel,
        color = color ?: DEFAULT_EVENT_COLOR,
    )
}

private fun LocalDateTime.formatTime(): String =
    "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"
