package org.bmsk.lifemash.profile.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import org.bmsk.lifemash.domain.calendar.Event
import org.bmsk.lifemash.domain.profile.CalendarDayEvent
import org.bmsk.lifemash.domain.profile.CalendarViewMode
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

    private var groupId: String? = null

    fun loadProfile(userId: String) {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
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
                            selectedYear = now.year,
                            selectedMonth = now.month.number,
                            selectedSubTab = settings.defaultSubTab,
                            calendarViewMode = settings.myCalendarViewMode,
                        )
                    }
                    loadMoments(userId)
                    loadGroupAndEvents(now.year, now.month.number)
                }
        }
    }

    private fun loadMoments(userId: String) {
        viewModelScope.launch {
            runCatching { getProfileMomentsUseCase(userId) }
                .onSuccess { moments ->
                    _uiState.update { it.copy(moments = moments) }
                }
        }
    }

    private fun loadGroupAndEvents(year: Int, month: Int) {
        viewModelScope.launch {
            runCatching {
                val groups = getMyGroupsUseCase()
                val firstGroup = groups.firstOrNull() ?: return@runCatching
                groupId = firstGroup.id
                loadEvents(firstGroup.id, year, month)
            }
        }
    }

    private suspend fun loadEvents(gId: String, year: Int, month: Int) {
        runCatching {
            val events = getMonthEventsUseCase(gId, year, month)
            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            val today = LocalDate(now.year, now.month, now.day)

            val calendarMap = events.groupBy { event ->
                event.startAt.toLocalDateTime(TimeZone.currentSystemDefault()).date.day
            }.mapValues { (_, dayEvents) ->
                dayEvents.map { it.toCalendarDayEvent() }
            }

            val dayEventsMap = events.groupBy { event ->
                event.startAt.toLocalDateTime(TimeZone.currentSystemDefault()).date.day
            }.mapValues { (_, dayEvents) ->
                dayEvents.map { it.toProfileEvent() }
            }

            val todayEvents = events.filter { event ->
                event.startAt.toLocalDateTime(TimeZone.currentSystemDefault()).date == today
            }.map { it.toProfileEvent() }

            _uiState.update {
                it.copy(
                    calendarEvents = calendarMap,
                    dayEvents = dayEventsMap,
                    todayEvents = todayEvents,
                )
            }
        }
    }

    fun reloadEvents() {
        val gId = groupId ?: return
        val state = _uiState.value
        viewModelScope.launch {
            loadEvents(gId, state.selectedYear, state.selectedMonth)
        }
    }

    fun selectSubTab(tab: ProfileSubTab) {
        _uiState.update { it.copy(selectedSubTab = tab) }
    }

    fun selectCalendarDay(day: Int?) {
        _uiState.update { it.copy(selectedCalendarDay = day) }
    }

    fun navigateMonth(delta: Int) {
        _uiState.update { state ->
            var month = state.selectedMonth + delta
            var year = state.selectedYear
            if (month > 12) { month = 1; year++ }
            if (month < 1) { month = 12; year-- }
            state.copy(selectedYear = year, selectedMonth = month)
        }
        val state = _uiState.value
        val gId = groupId
        if (gId != null) {
            viewModelScope.launch {
                loadEvents(gId, state.selectedYear, state.selectedMonth)
            }
        }
    }

    fun updateEvent(eventId: String, title: String?, color: String?) {
        val gId = groupId ?: return
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
        val gId = groupId ?: return
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

    private fun Event.toCalendarDayEvent() = CalendarDayEvent(
        id = id,
        title = title,
        color = color ?: "#4F6AF5",
    )

    private fun Event.toProfileEvent(): ProfileEvent {
        val tz = TimeZone.currentSystemDefault()
        val start = startAt.toLocalDateTime(tz)
        val end = endAt?.toLocalDateTime(tz)
        return ProfileEvent(
            id = id,
            title = title,
            startTime = "${start.hour.toString().padStart(2, '0')}:${start.minute.toString().padStart(2, '0')}",
            endTime = end?.let { "${it.hour.toString().padStart(2, '0')}:${it.minute.toString().padStart(2, '0')}" } ?: "",
            color = color ?: "#4F6AF5",
        )
    }
}
