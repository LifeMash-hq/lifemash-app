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
    val groupId: String?,
    val moments: List<Moment>,
    val calendarEvents: Map<Int, List<CalendarDayEvent>>,
    val dayEvents: Map<Int, List<ProfileEvent>>,
    val todayDay: Int,
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
    // ─── 파생 속성 ──────────────────────────────────────────────────────────

    val isReady: Boolean by lazy { screenPhase is ScreenPhase.Ready }

    val todayEvents: List<ProfileEvent> by lazy {
        dayEvents[todayDay] ?: emptyList()
    }

    val selectedDayLabel: String by lazy {
        if (selectedCalendarDay != null) "${selectedMonth}월 ${selectedCalendarDay}일"
        else "오늘"
    }

    val selectedDayEvents: List<ProfileEvent> by lazy {
        if (selectedCalendarDay != null) dayEvents[selectedCalendarDay] ?: emptyList()
        else todayEvents
    }

    // ─── 변환 함수 ──────────────────────────────────────────────────────────

    fun withMonthDelta(delta: Int): ProfileUiState {
        var month = selectedMonth + delta
        var year = selectedYear
        if (month > 12) { month = 1; year++ }
        if (month < 1) { month = 12; year-- }
        return copy(selectedYear = year, selectedMonth = month, selectedCalendarDay = null)
    }

    companion object {
        val Default = ProfileUiState(
            screenPhase = ScreenPhase.Initializing,
            profile = null,
            groupId = null,
            moments = emptyList(),
            calendarEvents = emptyMap(),
            dayEvents = emptyMap(),
            todayDay = 0,
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
