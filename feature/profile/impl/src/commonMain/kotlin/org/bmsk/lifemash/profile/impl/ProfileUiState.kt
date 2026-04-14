package org.bmsk.lifemash.profile.impl

import org.bmsk.lifemash.domain.moment.Moment
import org.bmsk.lifemash.domain.profile.CalendarDayEvent
import org.bmsk.lifemash.domain.profile.CalendarViewMode
import org.bmsk.lifemash.domain.profile.ProfileEvent
import org.bmsk.lifemash.domain.profile.ProfileSubTab
import org.bmsk.lifemash.domain.profile.UserProfile

sealed interface ScreenPhase {
    data object Initializing : ScreenPhase
    data object Ready : ScreenPhase
    data class FatalError(val message: String) : ScreenPhase
}

data class ProfileUiState(
    val screenPhase: ScreenPhase,
    val profile: UserProfile?,
    val moments: List<Moment>,
    val todayEvents: List<ProfileEvent>,
    val calendarEvents: Map<Int, List<CalendarDayEvent>>,
    val dayEvents: Map<Int, List<ProfileEvent>>,
    val selectedYear: Int,
    val selectedMonth: Int,
    val selectedSubTab: ProfileSubTab,
    val selectedCalendarDay: Int?,
    val calendarViewMode: CalendarViewMode,
    val isFollowInProgress: Boolean,
    val isFollowSheetVisible: Boolean,
    val errorMessage: String?,
    val event: ProfileUiEvent?,
) {
    val isReady: Boolean by lazy { screenPhase is ScreenPhase.Ready }

    companion object {
        val Default = ProfileUiState(
            screenPhase = ScreenPhase.Initializing,
            profile = null,
            moments = emptyList(),
            todayEvents = emptyList(),
            calendarEvents = emptyMap(),
            dayEvents = emptyMap(),
            selectedYear = 0,
            selectedMonth = 0,
            selectedSubTab = ProfileSubTab.MOMENTS,
            selectedCalendarDay = null,
            calendarViewMode = CalendarViewMode.DOT,
            isFollowInProgress = false,
            isFollowSheetVisible = false,
            errorMessage = null,
            event = null,
        )
    }
}

sealed interface ProfileUiEvent {
    data object NavigateBack : ProfileUiEvent
}
