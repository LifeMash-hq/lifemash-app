package org.bmsk.lifemash.calendar.impl

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

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

    fun timeLabel(): String = when {
        startTime != null && endTime != null -> "${startTime.formatted()} - ${endTime.formatted()}"
        startTime != null -> startTime.formatted()
        else -> "시간 추가"
    }

    fun toStartInstant(tz: TimeZone): Instant {
        val hour = startTime?.hour ?: 0
        val minute = startTime?.minute ?: 0
        return LocalDateTime(date, LocalTime(hour, minute)).toInstant(tz)
    }

    fun toEndInstant(tz: TimeZone): Instant? {
        val end = endTime ?: return null
        return LocalDateTime(date, LocalTime(end.hour, end.minute)).toInstant(tz)
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
