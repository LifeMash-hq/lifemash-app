package org.bmsk.lifemash.calendar.ui

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.datetime.LocalDate
import org.bmsk.lifemash.calendar.domain.model.Event
import org.bmsk.lifemash.calendar.domain.model.Group

internal data class CalendarUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val currentYear: Int = 0,
    val currentMonth: Int = 0,
    val selectedDate: LocalDate? = null,
    val groups: PersistentList<Group> = persistentListOf(),
    val selectedGroup: Group? = null,
    val overlay: CalendarOverlay = CalendarOverlay.None,
    val events: PersistentList<Event> = persistentListOf(),
    val isCreatingGroup: Boolean = false,
    val isRenamingGroup: Boolean = false,
) {
    enum class ScreenType { Loading, NoGroup, Calendar }

    val screenType: ScreenType get() = when {
        isLoading && groups.isEmpty() -> ScreenType.Loading
        groups.isEmpty() -> ScreenType.NoGroup
        else -> ScreenType.Calendar
    }
}
