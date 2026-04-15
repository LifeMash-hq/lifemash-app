@file:OptIn(kotlin.time.ExperimentalTime::class)

package org.bmsk.lifemash.domain.calendar

import kotlinx.datetime.LocalDate
import kotlin.time.Instant

sealed interface EventTiming {
    data class Timed(val start: Instant, val end: Instant) : EventTiming
    data class AllDay(val date: LocalDate) : EventTiming
}
