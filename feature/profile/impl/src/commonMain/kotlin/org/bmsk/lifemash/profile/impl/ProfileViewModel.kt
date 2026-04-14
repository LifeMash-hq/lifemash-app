package org.bmsk.lifemash.profile.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import org.bmsk.lifemash.domain.calendar.Event
import org.bmsk.lifemash.domain.profile.CalendarDayEvent
import org.bmsk.lifemash.domain.profile.ProfileEvent
import org.bmsk.lifemash.domain.profile.ProfileSettings
import org.bmsk.lifemash.domain.profile.ProfileSubTab
import org.bmsk.lifemash.domain.usecase.calendar.DeleteEventUseCase
import org.bmsk.lifemash.domain.usecase.calendar.GetMonthEventsUseCase
import org.bmsk.lifemash.domain.usecase.calendar.GetMyGroupsUseCase
import org.bmsk.lifemash.domain.usecase.calendar.UpdateEventUseCase
import org.bmsk.lifemash.domain.usecase.follow.FollowUserUseCase
import org.bmsk.lifemash.domain.usecase.follow.UnfollowUserUseCase
import org.bmsk.lifemash.domain.usecase.profile.GetProfileMomentsUseCase
import org.bmsk.lifemash.domain.usecase.profile.GetProfileSettingsUseCase
import org.bmsk.lifemash.domain.usecase.profile.GetUserProfileUseCase
import kotlin.time.Clock

internal class ProfileViewModel(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getProfileSettingsUseCase: GetProfileSettingsUseCase,
    private val getProfileMomentsUseCase: GetProfileMomentsUseCase,
    private val getMyGroupsUseCase: GetMyGroupsUseCase,
    private val getMonthEventsUseCase: GetMonthEventsUseCase,
    private val updateEventUseCase: UpdateEventUseCase,
    private val deleteEventUseCase: DeleteEventUseCase,
    private val followUserUseCase: FollowUserUseCase,
    private val unfollowUserUseCase: UnfollowUserUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState.Default)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun loadProfile(userId: String) {
        val tz = TimeZone.currentSystemDefault()
        val now = Clock.System.now().toLocalDateTime(tz)
        viewModelScope.launch {
            val settings = runCatching { getProfileSettingsUseCase() }
                .getOrElse { ProfileSettings.Default }

            getUserProfileUseCase(userId)
                .catch { e ->
                    _uiState.update {
                        it.copy(screenPhase = ScreenPhase.FatalError(e.message ?: "알 수 없는 오류"))
                    }
                }
                .collect { profile ->
                    _uiState.update {
                        it.copy(
                            screenPhase = ScreenPhase.Ready,
                            profile = profile,
                            todayDay = now.day,
                            selectedYear = now.year,
                            selectedMonth = now.month.number,
                            selectedSubTab = settings.defaultSubTab,
                            calendarViewMode = settings.myCalendarViewMode,
                        )
                    }

                    runCatching { getProfileMomentsUseCase(userId) }
                        .onSuccess { moments ->
                            _uiState.update { it.copy(moments = moments) }
                        }

                    runCatching { fetchGroupId() }
                        .onSuccess { gId ->
                            if (gId == null) return@onSuccess
                            _uiState.update { it.copy(groupId = gId) }
                            runCatching { fetchEventsData(gId, now.year, now.month.number) }
                                .onSuccess { data ->
                                    _uiState.update {
                                        it.copy(calendarEvents = data.calendarEvents, dayEvents = data.dayEvents)
                                    }
                                }
                        }
                }
        }
    }

    fun reloadEvents() {
        val state = _uiState.value
        val gId = state.groupId ?: return
        viewModelScope.launch {
            runCatching { fetchEventsData(gId, state.selectedYear, state.selectedMonth) }
                .onSuccess { data ->
                    _uiState.update {
                        it.copy(calendarEvents = data.calendarEvents, dayEvents = data.dayEvents)
                    }
                }
        }
    }

    fun selectSubTab(tab: ProfileSubTab) {
        _uiState.update { it.copy(selectedSubTab = tab) }
    }

    fun selectCalendarDay(day: Int?) {
        _uiState.update { it.copy(selectedCalendarDay = day) }
    }

    fun navigateMonth(delta: Int) {
        _uiState.update { it.withMonthDelta(delta) }
        val state = _uiState.value
        val gId = state.groupId ?: return
        viewModelScope.launch {
            runCatching { fetchEventsData(gId, state.selectedYear, state.selectedMonth) }
                .onSuccess { data ->
                    _uiState.update {
                        it.copy(calendarEvents = data.calendarEvents, dayEvents = data.dayEvents)
                    }
                }
        }
    }

    fun updateEvent(eventId: String, title: String?, color: String?) {
        val gId = _uiState.value.groupId ?: return
        viewModelScope.launch {
            runCatching {
                updateEventUseCase(
                    groupId = gId,
                    eventId = eventId,
                    title = title,
                    description = null,
                    location = null,
                    startAt = null,
                    endAt = null,
                    isAllDay = null,
                    color = color,
                )
            }.onSuccess {
                reloadEvents()
            }.onFailure { e ->
                _uiState.update { it.copy(errorMessage = e.message ?: "일정 수정 실패") }
            }
        }
    }

    fun deleteEvent(eventId: String) {
        val gId = _uiState.value.groupId ?: return
        viewModelScope.launch {
            runCatching {
                deleteEventUseCase(gId, eventId)
            }.onSuccess {
                reloadEvents()
            }.onFailure { e ->
                _uiState.update { it.copy(errorMessage = e.message ?: "일정 삭제 실패") }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun toggleFollow(userId: String) {
        if (_uiState.value.isFollowInProgress) return
        val wasFollowing = _uiState.value.profile?.isFollowing ?: return

        _uiState.value = _uiState.value.copy(
            isFollowInProgress = true,
            profile = _uiState.value.profile?.copy(isFollowing = !wasFollowing),
        )

        viewModelScope.launch {
            runCatching {
                if (wasFollowing) unfollowUserUseCase(userId) else followUserUseCase(userId)
            }.onSuccess {
                _uiState.update { it.copy(isFollowInProgress = false) }
            }.onFailure {
                _uiState.update {
                    it.copy(
                        isFollowInProgress = false,
                        profile = it.profile?.copy(isFollowing = wasFollowing),
                    )
                }
            }
        }
    }

    fun showFollowSheet() {
        _uiState.update { it.copy(isFollowSheetVisible = true) }
    }

    fun dismissFollowSheet() {
        _uiState.update { it.copy(isFollowSheetVisible = false) }
    }

    fun consumeEvent() {
        _uiState.update { it.copy(event = null) }
    }

    // ─── private: 데이터 fetch만, 상태 변경 없음 ────────────────────────────

    private suspend fun fetchGroupId(): String? {
        val groups = getMyGroupsUseCase()
        return groups.firstOrNull()?.id
    }

    private suspend fun fetchEventsData(
        gId: String,
        year: Int,
        month: Int,
    ): EventsData {
        val events = getMonthEventsUseCase(gId, year, month)
        val tz = TimeZone.currentSystemDefault()
        val grouped = events.groupBy { it.startAt.toLocalDateTime(tz).date.day }
        return EventsData(
            calendarEvents = grouped.mapValues { (_, v) -> v.map { e -> e.toCalendarDayEvent() } },
            dayEvents = grouped.mapValues { (_, v) -> v.map { e -> e.toProfileEvent() } },
        )
    }
}

private data class EventsData(
    val calendarEvents: Map<Int, List<CalendarDayEvent>>,
    val dayEvents: Map<Int, List<ProfileEvent>>,
)
