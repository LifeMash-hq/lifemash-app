package org.bmsk.lifemash.profile.impl

import org.bmsk.lifemash.domain.profile.CalendarDayEvent
import org.bmsk.lifemash.domain.profile.Moment
import org.bmsk.lifemash.domain.profile.ProfileEvent
import org.bmsk.lifemash.domain.profile.UserProfile

enum class ProfileSubTab { Moments, Calendar }
enum class CalendarViewMode { Dot, Chip }

sealed interface ProfileUiState {
    data object Loading : ProfileUiState
    data class Loaded(
        val profile: UserProfile,
        val moments: List<Moment> = emptyList(),
        val followers: List<UserProfile> = emptyList(),
        val following: List<UserProfile> = emptyList(),
        val todayEvents: List<ProfileEvent> = emptyList(),
        val calendarEvents: Map<Int, List<CalendarDayEvent>> = emptyMap(),
        val dayEvents: Map<Int, List<ProfileEvent>> = emptyMap(),
        val selectedYear: Int = 0,
        val selectedMonth: Int = 0,
        val selectedSubTab: ProfileSubTab = ProfileSubTab.Moments,
        val selectedCalendarDay: Int? = null,
        val calendarViewMode: CalendarViewMode = CalendarViewMode.Dot,
        val errorMessage: String? = null,
    ) : ProfileUiState
    data class Error(val message: String) : ProfileUiState
}
