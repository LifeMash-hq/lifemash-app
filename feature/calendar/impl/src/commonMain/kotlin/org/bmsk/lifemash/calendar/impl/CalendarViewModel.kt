package org.bmsk.lifemash.calendar.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.bmsk.lifemash.domain.calendar.GroupType
import org.bmsk.lifemash.domain.usecase.calendar.CreateGroupUseCase
import org.bmsk.lifemash.domain.usecase.calendar.DeleteEventUseCase
import org.bmsk.lifemash.domain.usecase.calendar.GetMonthEventsUseCase
import org.bmsk.lifemash.domain.usecase.calendar.GetMyGroupsUseCase
import org.bmsk.lifemash.domain.usecase.calendar.JoinGroupUseCase
import org.bmsk.lifemash.domain.usecase.calendar.UpdateGroupNameUseCase

internal class CalendarViewModel(
    private val getMonthEvents: GetMonthEventsUseCase,
    private val getMyGroups: GetMyGroupsUseCase,
    private val createGroup: CreateGroupUseCase,
    private val joinGroup: JoinGroupUseCase,
    private val updateGroupName: UpdateGroupNameUseCase,
    private val deleteEvent: DeleteEventUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarUiState.Default)
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    fun selectDate(date: LocalDate) {
        _uiState.update { it.copy(selectedDate = date) }
    }

    fun changeMonth(
        groupId: String,
        year: Int,
        month: Int,
    ) {
        _uiState.update { it.copy(currentYear = year, currentMonth = month) }
        viewModelScope.launch {
            runCatching {
                getMonthEvents(groupId, year, month)
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
                getMonthEvents(groupId, state.currentYear, state.currentMonth)
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
                val group = this@CalendarViewModel.createGroup.invoke(type, name)
                _uiState.update { state ->
                    state.copy(
                        groups = (state.groups.toList() + group).toPersistentList(),
                        selectedGroup = group,
                        isCreatingGroup = false,
                    )
                }
                val state = _uiState.value
                val events = getMonthEvents(group.id, state.currentYear, state.currentMonth)
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
                val group = this@CalendarViewModel.joinGroup.invoke(inviteCode)
                _uiState.update { state ->
                    state.copy(
                        groups = (state.groups.toList() + group).toPersistentList(),
                        selectedGroup = group,
                        isCreatingGroup = false,
                    )
                }
                val state = _uiState.value
                val events = getMonthEvents(group.id, state.currentYear, state.currentMonth)
                _uiState.update { it.copy(events = events.toPersistentList()) }
            }.onFailure { e ->
                _uiState.update { it.copy(isCreatingGroup = false, errorMessage = e.message ?: "그룹 참여 실패") }
            }
        }
    }

    fun refreshEvents() {
        val state = _uiState.value
        val groupId = state.selectedGroup?.id ?: return
        viewModelScope.launch {
            runCatching {
                val events = getMonthEvents(groupId, state.currentYear, state.currentMonth)
                _uiState.update { it.copy(events = events.toPersistentList()) }
            }.onFailure { e ->
                _uiState.update { it.copy(errorMessage = e.message ?: "일정 로드 실패") }
            }
        }
    }

    fun deleteEvent(groupId: String, eventId: String) {
        viewModelScope.launch {
            runCatching {
                this@CalendarViewModel.deleteEvent.invoke(groupId, eventId)
                _uiState.update { it.copy(overlay = CalendarOverlay.None) }
                val state = _uiState.value
                val events = getMonthEvents(groupId, state.currentYear, state.currentMonth)
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
                val updatedGroup = this@CalendarViewModel.updateGroupName.invoke(groupId, name)
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
                val groups = getMyGroups()
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
                    val events = getMonthEvents(group.id, now.year, monthInt)
                    _uiState.update { it.copy(events = events.toPersistentList()) }
                }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "그룹 로드 실패") }
            }
        }
    }
}
