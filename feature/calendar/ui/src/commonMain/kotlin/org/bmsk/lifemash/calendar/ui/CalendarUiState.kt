package org.bmsk.lifemash.calendar.ui

import kotlinx.collections.immutable.PersistentList
import kotlinx.datetime.LocalDate
import org.bmsk.lifemash.calendar.domain.model.Event
import org.bmsk.lifemash.calendar.domain.model.Group

internal data class CalendarUiState(
    val isLoading: Boolean = true,
    val currentYear: Int = 2026,
    val currentMonth: Int = 3,
    val selectedDate: LocalDate? = null,
    val events: PersistentList<Event>? = null,
    val selectedGroup: Group? = null,
    val groups: PersistentList<Group>? = null,
    val error: String? = null,
)
