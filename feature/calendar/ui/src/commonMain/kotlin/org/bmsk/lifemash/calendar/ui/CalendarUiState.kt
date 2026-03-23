package org.bmsk.lifemash.calendar.ui

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.datetime.LocalDate
import org.bmsk.lifemash.calendar.domain.model.Event
import org.bmsk.lifemash.calendar.domain.model.Group

internal sealed interface CalendarUiState {
    data object Loading : CalendarUiState
    data class Loaded(
        val currentYear: Int,
        val currentMonth: Int,
        val selectedDate: LocalDate?,
        val events: PersistentList<Event> = persistentListOf(),
        val groups: PersistentList<Group> = persistentListOf(),
        val selectedGroup: Group? = null,
        val overlay: CalendarOverlay = CalendarOverlay.None,
        val isCreatingGroup: Boolean = false,
        val isCreatingEvent: Boolean = false,
        val isRenamingGroup: Boolean = false,
    ) : CalendarUiState
    data class Error(val message: String) : CalendarUiState
}
