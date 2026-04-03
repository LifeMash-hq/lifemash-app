package org.bmsk.lifemash.profile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import org.bmsk.lifemash.calendar.domain.model.Event
import org.bmsk.lifemash.calendar.domain.repository.EventRepository
import org.bmsk.lifemash.calendar.domain.repository.GroupRepository
import org.bmsk.lifemash.profile.domain.model.CalendarDayEvent
import org.bmsk.lifemash.profile.domain.model.Moment
import org.bmsk.lifemash.profile.domain.model.ProfileEvent
import org.bmsk.lifemash.profile.domain.model.ProfileSettings
import org.bmsk.lifemash.profile.domain.model.UserProfile
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

internal class ProfileViewModel(
    private val profileRepository: ProfileRepository,
    private val eventRepository: EventRepository,
    private val groupRepository: GroupRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState

    private var groupId: String? = null

    fun loadProfile(userId: String) {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        viewModelScope.launch {
            val settings = runCatching { profileRepository.getProfileSettings() }
                .getOrElse { ProfileSettings() }

            val defaultSubTab = if (settings.defaultSubTab == "calendar") ProfileSubTab.Calendar else ProfileSubTab.Moments
            val viewMode = if (settings.myCalendarViewMode == "chip") CalendarViewMode.Chip else CalendarViewMode.Dot

            profileRepository.getProfile(userId)
                .catch { _uiState.value = ProfileUiState.Error(it.message ?: "알 수 없는 오류") }
                .collect { profile ->
                    _uiState.value = ProfileUiState.Loaded(
                        profile = profile,
                        selectedYear = now.year,
                        selectedMonth = now.month.number,
                        selectedSubTab = defaultSubTab,
                        calendarViewMode = viewMode,
                    )
                    loadMoments(userId)
                    loadGroupAndEvents(now.year, now.month.number)
                }
        }
    }

    private fun loadMoments(userId: String) {
        viewModelScope.launch {
            profileRepository.getMoments(userId)
                .catch { e -> e.printStackTrace() }
                .collect { moments ->
                    _uiState.update { state ->
                        if (state is ProfileUiState.Loaded) state.copy(moments = moments) else state
                    }
                }
        }
    }

    private fun loadGroupAndEvents(year: Int, month: Int) {
        viewModelScope.launch {
            runCatching {
                val groups = groupRepository.getMyGroups()
                val firstGroup = groups.firstOrNull() ?: return@runCatching
                groupId = firstGroup.id
                loadEvents(firstGroup.id, year, month)
            }.onFailure { /* 그룹이 없으면 무시 */ }
        }
    }

    private suspend fun loadEvents(gId: String, year: Int, month: Int) {
        runCatching {
            val events = eventRepository.getMonthEvents(gId, year, month)
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

            _uiState.update { state ->
                if (state is ProfileUiState.Loaded) {
                    state.copy(calendarEvents = calendarMap, dayEvents = dayEventsMap, todayEvents = todayEvents)
                } else state
            }
        }.onFailure { /* 이벤트 로드 실패 무시 */ }
    }

    fun reloadEvents() {
        val state = _uiState.value as? ProfileUiState.Loaded ?: return
        val gId = groupId ?: return
        viewModelScope.launch {
            loadEvents(gId, state.selectedYear, state.selectedMonth)
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
        val state = _uiState.value
        val gId = groupId
        if (state is ProfileUiState.Loaded && gId != null) {
            viewModelScope.launch {
                loadEvents(gId, state.selectedYear, state.selectedMonth)
            }
        }
    }

    fun updateEvent(eventId: String, title: String?, color: String?) {
        val gId = groupId ?: return
        viewModelScope.launch {
            runCatching {
                // 프로필 화면 인라인 편집은 title/color만 수정. null = 기존 값 유지.
                eventRepository.updateEvent(
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
                _uiState.update { state ->
                    if (state is ProfileUiState.Loaded) state.copy(errorMessage = e.message ?: "일정 수정 실패")
                    else state
                }
            }
        }
    }

    fun deleteEvent(eventId: String) {
        val gId = groupId ?: return
        viewModelScope.launch {
            runCatching {
                eventRepository.deleteEvent(gId, eventId)
            }.onSuccess {
                reloadEvents()
            }.onFailure { e ->
                _uiState.update { state ->
                    if (state is ProfileUiState.Loaded) state.copy(errorMessage = e.message ?: "일정 삭제 실패")
                    else state
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { state ->
            if (state is ProfileUiState.Loaded) state.copy(errorMessage = null) else state
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
