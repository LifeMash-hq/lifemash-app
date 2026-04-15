package org.bmsk.lifemash.calendar.impl

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.bmsk.lifemash.domain.calendar.EventTiming

data class TimeOfDay(val hour: Int, val minute: Int) {
    fun formatted(): String =
        "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"
}

data class EventDateTime(
    val date: LocalDate,
    val startTime: TimeOfDay? = null,
    val endTime: TimeOfDay? = null,
) {
    val isAllDay: Boolean get() = startTime == null

    fun dateLabel(): String {
        val dow = when (date.dayOfWeek.name) {
            "MONDAY" -> "월"
            "TUESDAY" -> "화"
            "WEDNESDAY" -> "수"
            "THURSDAY" -> "목"
            "FRIDAY" -> "금"
            "SATURDAY" -> "토"
            "SUNDAY" -> "일"
            else -> ""
        }
        return "${date.month.number}월 ${date.day}일 ($dow)"
    }

    fun timeLabel(): String =
        if (startTime != null && endTime != null) "${startTime.formatted()} - ${endTime.formatted()}"
        else "시간 추가"

    fun withStartTime(value: TimeOfDay?): EventDateTime {
        if (value == null) return copy(startTime = null, endTime = null)
        val nextEnd = endTime ?: TimeOfDay((value.hour + 1) % 24, value.minute)
        return copy(startTime = value, endTime = nextEnd)
    }

    fun withEndTime(value: TimeOfDay?): EventDateTime = copy(endTime = value ?: endTime)

    fun toTiming(tz: TimeZone): EventTiming {
        val start = startTime ?: return EventTiming.AllDay(date)
        val end = endTime ?: TimeOfDay((start.hour + 1) % 24, start.minute)
        return EventTiming.Timed(
            start = LocalDateTime(date, LocalTime(start.hour, start.minute)).toInstant(tz),
            end = LocalDateTime(date, LocalTime(end.hour, end.minute)).toInstant(tz),
        )
    }

    companion object {
        fun now(): EventDateTime {
            val tz = TimeZone.currentSystemDefault()
            val today = kotlin.time.Clock.System.now().toLocalDateTime(tz).date
            return EventDateTime(date = today)
        }

        fun of(year: Int, month: Int, day: Int): EventDateTime =
            EventDateTime(date = LocalDate(year, month, day))
    }
}
