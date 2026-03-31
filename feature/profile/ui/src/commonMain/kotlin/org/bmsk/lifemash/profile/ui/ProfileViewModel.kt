package org.bmsk.lifemash.profile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import org.bmsk.lifemash.profile.domain.model.CalendarDayEvent
import org.bmsk.lifemash.profile.domain.model.Moment
import org.bmsk.lifemash.profile.domain.model.ProfileEvent
import org.bmsk.lifemash.profile.domain.model.UserProfile
import org.bmsk.lifemash.profile.domain.repository.MomentRepository
import org.bmsk.lifemash.profile.domain.repository.ProfileRepository

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
        val selectedYear: Int = 0,
        val selectedMonth: Int = 0,
        val selectedSubTab: ProfileSubTab = ProfileSubTab.Moments,
        val selectedCalendarDay: Int? = null,
        val calendarViewMode: CalendarViewMode = CalendarViewMode.Dot,
    ) : ProfileUiState
    data class Error(val message: String) : ProfileUiState
}

internal class ProfileViewModel(
    private val profileRepository: ProfileRepository,
    private val momentRepository: MomentRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState

    fun loadProfile(userId: String) {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        viewModelScope.launch {
            profileRepository.getProfile(userId)
                .catch { _uiState.value = ProfileUiState.Error(it.message ?: "알 수 없는 오류") }
                .collect { profile ->
                    _uiState.value = ProfileUiState.Loaded(
                        profile = profile,
                        selectedYear = now.year,
                        selectedMonth = now.month.number,
                    )
                    loadMoments(userId)
                }
        }
    }

    private fun loadMoments(userId: String) {
        viewModelScope.launch {
            momentRepository.getMoments(userId)
                .catch { e -> e.printStackTrace() }
                .collect { moments ->
                    _uiState.update { state ->
                        if (state is ProfileUiState.Loaded) state.copy(moments = moments) else state
                    }
                }
        }
    }

    fun selectSubTab(tab: ProfileSubTab) {
        _uiState.update { state ->
            if (state is ProfileUiState.Loaded) state.copy(selectedSubTab = tab) else state
        }
    }

    fun selectCalendarDay(day: Int?) {
        _uiState.update { state ->
            if (state is ProfileUiState.Loaded) state.copy(selectedCalendarDay = day) else state
        }
    }

    fun navigateMonth(delta: Int) {
        _uiState.update { state ->
            if (state is ProfileUiState.Loaded) {
                var month = state.selectedMonth + delta
                var year = state.selectedYear
                if (month > 12) { month = 1; year++ }
                if (month < 1) { month = 12; year-- }
                state.copy(selectedYear = year, selectedMonth = month)
            } else state
        }
    }

    fun toggleFollow(loaded: ProfileUiState.Loaded, userId: String) {
        val wasFollowing = loaded.profile.isFollowing
        _uiState.value = loaded.copy(profile = loaded.profile.copy(isFollowing = !wasFollowing))
        viewModelScope.launch {
            runCatching {
                if (wasFollowing) profileRepository.unfollow(userId) else profileRepository.follow(userId)
            }.onFailure {
                _uiState.update { state ->
                    if (state is ProfileUiState.Loaded)
                        state.copy(profile = state.profile.copy(isFollowing = wasFollowing))
                    else state
                }
            }
        }
    }
}
