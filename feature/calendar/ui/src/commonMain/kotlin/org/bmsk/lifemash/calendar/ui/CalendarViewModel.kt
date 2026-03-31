package org.bmsk.lifemash.calendar.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.bmsk.lifemash.calendar.domain.model.GroupType
import org.bmsk.lifemash.calendar.domain.repository.EventRepository
import org.bmsk.lifemash.calendar.domain.repository.GroupRepository

internal class CalendarViewModel(
    private val eventRepository: EventRepository,
    private val groupRepository: GroupRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    fun selectDate(date: LocalDate) {
        _uiState.update { it.copy(selectedDate = date) }
    }

    fun changeMonth(groupId: String, year: Int, month: Int) {
        _uiState.update { it.copy(currentYear = year, currentMonth = month) }
        viewModelScope.launch {
            runCatching {
                eventRepository.getMonthEvents(groupId, year, month)
            }.onFailure { e ->
                _uiState.update { it.copy(errorMessage = e.message ?: "일정 로드 실패") }
            }.onSuccess { events ->
                _uiState.update { it.copy(events = events.toPersistentList()) }
            }
        }
    }

    fun selectGroup(groupId: String) {
        _uiState.update { state ->
            state.copy(selectedGroup = state.groups.find { it.id == groupId })
        }
        val state = _uiState.value
        viewModelScope.launch {
            runCatching {
                eventRepository.getMonthEvents(groupId, state.currentYear, state.currentMonth)
            }.onFailure { e ->
                _uiState.update { it.copy(errorMessage = e.message ?: "일정 로드 실패") }
            }.onSuccess { events ->
                _uiState.update { it.copy(events = events.toPersistentList()) }
            }
        }
    }

    fun showOverlay(overlay: CalendarOverlay) {
        _uiState.update { it.copy(overlay = overlay) }
    }

    fun dismissOverlay() {
        _uiState.update { it.copy(overlay = CalendarOverlay.None) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun createGroup(type: GroupType = GroupType.COUPLE, name: String? = null) {
        _uiState.update { it.copy(isCreatingGroup = true) }
        viewModelScope.launch {
            runCatching {
                val group = groupRepository.createGroup(type, name)
                _uiState.update { state ->
                    state.copy(
                        groups = (state.groups.toList() + group).toPersistentList(),
                        selectedGroup = group,
                        isCreatingGroup = false,
                    )
                }
                val state = _uiState.value
                val events = eventRepository.getMonthEvents(group.id, state.currentYear, state.currentMonth)
                _uiState.update { it.copy(events = events.toPersistentList()) }
            }.onFailure { e ->
                _uiState.update { it.copy(isCreatingGroup = false, errorMessage = e.message ?: "그룹 생성 실패") }
            }
        }
    }

    fun joinGroup(inviteCode: String) {
        _uiState.update { it.copy(isCreatingGroup = true) }
        viewModelScope.launch {
            runCatching {
                val group = groupRepository.joinGroup(inviteCode)
                _uiState.update { state ->
                    state.copy(
                        groups = (state.groups.toList() + group).toPersistentList(),
                        selectedGroup = group,
                        isCreatingGroup = false,
                    )
                }
                val state = _uiState.value
                val events = eventRepository.getMonthEvents(group.id, state.currentYear, state.currentMonth)
                _uiState.update { it.copy(events = events.toPersistentList()) }
            }.onFailure { e ->
                _uiState.update { it.copy(isCreatingGroup = false, errorMessage = e.message ?: "그룹 참여 실패") }
            }
        }
    }

    fun createEvent(groupId: String, form: EventFormData) {
        _uiState.update { it.copy(isCreatingEvent = true) }
        viewModelScope.launch {
            runCatching {
                eventRepository.createEvent(
                    groupId = groupId,
                    title = form.title,
                    description = form.description,
                    startAt = Instant.fromEpochMilliseconds(form.startAt),
                    endAt = form.endAt?.let { Instant.fromEpochMilliseconds(it) },
                    isAllDay = form.isAllDay,
                    color = form.color,
                )
                _uiState.update { it.copy(isCreatingEvent = false, overlay = CalendarOverlay.None) }
                val state = _uiState.value
                val events = eventRepository.getMonthEvents(groupId, state.currentYear, state.currentMonth)
                _uiState.update { it.copy(events = events.toPersistentList()) }
            }.onFailure { e ->
                _uiState.update { it.copy(isCreatingEvent = false, errorMessage = e.message ?: "일정 생성 실패") }
            }
        }
    }

    fun updateEvent(groupId: String, eventId: String, form: EventFormData) {
        _uiState.update { it.copy(isCreatingEvent = true) }
        viewModelScope.launch {
            runCatching {
                eventRepository.updateEvent(
                    groupId = groupId,
                    eventId = eventId,
                    title = form.title,
                    description = form.description,
                    startAt = Instant.fromEpochMilliseconds(form.startAt),
                    endAt = form.endAt?.let { Instant.fromEpochMilliseconds(it) },
                    isAllDay = form.isAllDay,
                    color = form.color,
                )
                _uiState.update { it.copy(isCreatingEvent = false, overlay = CalendarOverlay.None) }
                val state = _uiState.value
                val events = eventRepository.getMonthEvents(groupId, state.currentYear, state.currentMonth)
                _uiState.update { it.copy(events = events.toPersistentList()) }
            }.onFailure { e ->
                _uiState.update { it.copy(isCreatingEvent = false, errorMessage = e.message ?: "일정 수정 실패") }
            }
        }
    }

    fun deleteEvent(groupId: String, eventId: String) {
        viewModelScope.launch {
            runCatching {
                eventRepository.deleteEvent(groupId, eventId)
                _uiState.update { it.copy(overlay = CalendarOverlay.None) }
                val state = _uiState.value
                val events = eventRepository.getMonthEvents(groupId, state.currentYear, state.currentMonth)
                _uiState.update { it.copy(events = events.toPersistentList()) }
            }.onFailure { e ->
                _uiState.update { it.copy(errorMessage = e.message ?: "일정 삭제 실패") }
            }
        }
    }

    fun updateGroupName(groupId: String, name: String) {
        _uiState.update { it.copy(isRenamingGroup = true) }
        viewModelScope.launch {
            runCatching {
                val updatedGroup = groupRepository.updateGroupName(groupId, name)
                _uiState.update { state ->
                    state.copy(
                        groups = state.groups.map {
                            if (it.id == updatedGroup.id) updatedGroup else it
                        }.toPersistentList(),
                        selectedGroup = updatedGroup,
                        overlay = CalendarOverlay.None,
                        isRenamingGroup = false,
                    )
                }
            }.onFailure { e ->
                _uiState.update { it.copy(isRenamingGroup = false, errorMessage = e.message ?: "그룹명 변경 실패") }
            }
        }
    }

    internal fun loadGroups() {
        viewModelScope.launch {
            runCatching {
                val groups = groupRepository.getMyGroups()
                val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                val monthInt = now.month.ordinal + 1
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        currentYear = now.year,
                        currentMonth = monthInt,
                        selectedDate = LocalDate(now.year, now.month, now.day),
                        groups = groups.toPersistentList(),
                        selectedGroup = groups.firstOrNull(),
                    )
                }
                groups.firstOrNull()?.let { group ->
                    val events = eventRepository.getMonthEvents(group.id, now.year, monthInt)
                    _uiState.update { it.copy(events = events.toPersistentList()) }
                }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "그룹 로드 실패") }
            }
        }
    }
}
