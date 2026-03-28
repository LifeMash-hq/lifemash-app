package org.bmsk.lifemash.profile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.bmsk.lifemash.profile.domain.model.Moment
import org.bmsk.lifemash.profile.domain.model.ProfileEvent
import org.bmsk.lifemash.profile.domain.model.UserProfile
import org.bmsk.lifemash.profile.domain.usecase.FollowUseCase
import org.bmsk.lifemash.profile.domain.usecase.GetMomentsUseCase
import org.bmsk.lifemash.profile.domain.usecase.GetProfileUseCase

sealed interface ProfileUiState {
    data object Loading : ProfileUiState
    data class Loaded(
        val profile: UserProfile,
        val moments: List<Moment> = emptyList(),
        val followers: List<UserProfile> = emptyList(),
        val following: List<UserProfile> = emptyList(),
        val todayEvents: List<ProfileEvent> = emptyList(),
        val calendarEventDates: Set<Int> = emptySet(), // day-of-month numbers with events
        val selectedYear: Int = 0,
        val selectedMonth: Int = 0,
    ) : ProfileUiState
    data class Error(val message: String) : ProfileUiState
}

class ProfileViewModel(
    private val getProfileUseCase: GetProfileUseCase,
    private val followUseCase: FollowUseCase,
    private val getMomentsUseCase: GetMomentsUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState

    fun loadProfile(userId: String) {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        viewModelScope.launch {
            getProfileUseCase(userId)
                .catch { _uiState.value = ProfileUiState.Error(it.message ?: "알 수 없는 오류") }
                .collect { profile ->
                    _uiState.value = ProfileUiState.Loaded(
                        profile = profile,
                        selectedYear = now.year,
                        selectedMonth = now.monthNumber,
                        todayEvents = listOf(
                            ProfileEvent("e1", "청담 오마카세", "19:00", "21:00", "#4F6AF5"),
                            ProfileEvent("e2", "밴드 연습", "14:00", "16:00", "#F5824F"),
                        ),
                        calendarEventDates = setOf(now.dayOfMonth, now.dayOfMonth + 2, now.dayOfMonth + 5),
                    )
                    loadMoments(userId)
                }
        }
    }

    private fun loadMoments(userId: String) {
        viewModelScope.launch {
            getMomentsUseCase(userId)
                .catch { /* ignore */ }
                .collect { moments ->
                    val current = _uiState.value as? ProfileUiState.Loaded ?: return@collect
                    _uiState.value = current.copy(moments = moments)
                }
        }
    }

    fun toggleFollow(userId: String) {
        val current = _uiState.value as? ProfileUiState.Loaded ?: return
        val wasFollowing = current.profile.isFollowing
        _uiState.value = current.copy(profile = current.profile.copy(isFollowing = !wasFollowing))
        viewModelScope.launch {
            runCatching {
                if (wasFollowing) followUseCase.unfollow(userId)
                else followUseCase.follow(userId)
            }.onFailure {
                val state = _uiState.value as? ProfileUiState.Loaded ?: return@launch
                _uiState.value = state.copy(profile = state.profile.copy(isFollowing = wasFollowing))
            }
        }
    }
}
