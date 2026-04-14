package org.bmsk.lifemash.calendar.impl

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.datetime.LocalDate
import org.bmsk.lifemash.domain.calendar.Event
import org.bmsk.lifemash.domain.calendar.Group

internal data class CalendarUiState(
    val isLoading: Boolean,
    val errorMessage: String?,
    val currentYear: Int,
    val currentMonth: Int,
    val selectedDate: LocalDate?,
    val groups: PersistentList<Group>,
    val selectedGroup: Group?,
    val overlay: CalendarOverlay,
    val events: PersistentList<Event>,
    val isCreatingGroup: Boolean,
    val isRenamingGroup: Boolean,
) {
    enum class ScreenType { Loading, NoGroup, Calendar }

    val screenType: ScreenType get() = when {
        isLoading && groups.isEmpty() -> ScreenType.Loading
        groups.isEmpty() -> ScreenType.NoGroup
        else -> ScreenType.Calendar
    }

    companion object {
        val Default = CalendarUiState(
            isLoading = true,
            errorMessage = null,
            currentYear = 0,
            currentMonth = 0,
            selectedDate = null,
            groups = persistentListOf(),
            selectedGroup = null,
            overlay = CalendarOverlay.None,
            events = persistentListOf(),
            isCreatingGroup = false,
            isRenamingGroup = false,
        )
    }
}
