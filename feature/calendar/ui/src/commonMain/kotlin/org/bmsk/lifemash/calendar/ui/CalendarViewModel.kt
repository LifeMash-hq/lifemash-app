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
import org.bmsk.lifemash.calendar.domain.model.Event
import org.bmsk.lifemash.calendar.domain.model.GroupType
import org.bmsk.lifemash.calendar.domain.repository.CreateEventRequest
import org.bmsk.lifemash.calendar.domain.repository.UpdateEventRequest
import org.bmsk.lifemash.calendar.domain.usecase.CreateEventUseCase
import org.bmsk.lifemash.calendar.domain.usecase.CreateGroupUseCase
import org.bmsk.lifemash.calendar.domain.usecase.DeleteEventUseCase
import org.bmsk.lifemash.calendar.domain.usecase.GetMonthEventsUseCase
import org.bmsk.lifemash.calendar.domain.usecase.GetMyGroupsUseCase
import org.bmsk.lifemash.calendar.domain.usecase.JoinGroupUseCase
import org.bmsk.lifemash.calendar.domain.usecase.UpdateEventUseCase

internal class CalendarViewModel(
    private val getMonthEventsUseCase: GetMonthEventsUseCase,
    private val getMyGroupsUseCase: GetMyGroupsUseCase,
    private val createGroupUseCase: CreateGroupUseCase,
    private val joinGroupUseCase: JoinGroupUseCase,
    private val createEventUseCase: CreateEventUseCase,
    private val updateEventUseCase: UpdateEventUseCase,
    private val deleteEventUseCase: DeleteEventUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(initialState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    init {
        loadGroups()
    }

    fun selectDate(date: LocalDate) {
        _uiState.update { it.copy(selectedDate = date) }
    }

    fun changeMonth(year: Int, month: Int) {
        _uiState.update { it.copy(currentYear = year, currentMonth = month) }
        loadEvents()
    }

    fun selectGroup(groupId: String) {
        val group = _uiState.value.groups?.find { it.id == groupId }
        _uiState.update { it.copy(selectedGroup = group) }
        loadEvents()
    }

    fun createGroup(type: GroupType = GroupType.COUPLE, name: String? = null) {
        _uiState.update { it.copy(isCreatingGroup = true) }
        viewModelScope.launch {
            runCatching { createGroupUseCase(type, name) }
                .onSuccess { group ->
                    _uiState.update {
                        val updatedGroups = (it.groups?.toList().orEmpty() + group).toPersistentList()
                        it.copy(
                            groups = updatedGroups,
                            selectedGroup = group,
                            isCreatingGroup = false,
                        )
                    }
                    loadEvents()
                }
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.message, isCreatingGroup = false) }
                }
        }
    }

    fun joinGroup(inviteCode: String) {
        _uiState.update { it.copy(isCreatingGroup = true) }
        viewModelScope.launch {
            runCatching { joinGroupUseCase(inviteCode) }
                .onSuccess { group ->
                    _uiState.update {
                        val updatedGroups = (it.groups?.toList().orEmpty() + group).toPersistentList()
                        it.copy(
                            groups = updatedGroups,
                            selectedGroup = group,
                            isCreatingGroup = false,
                        )
                    }
                    loadEvents()
                }
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.message, isCreatingGroup = false) }
                }
        }
    }

    fun showEventCreate() {
        _uiState.update { it.copy(showEventCreate = true, editingEvent = null) }
    }

    fun hideEventCreate() {
        _uiState.update { it.copy(showEventCreate = false, editingEvent = null) }
    }

    fun showEventDetail(event: Event) {
        _uiState.update { it.copy(selectedEvent = event, showEventDetail = true) }
    }

    fun hideEventDetail() {
        _uiState.update { it.copy(selectedEvent = null, showEventDetail = false) }
    }

    fun startEditEvent(event: Event) {
        _uiState.update { it.copy(showEventDetail = false, showEventCreate = true, editingEvent = event) }
    }

    fun createEvent(
        title: String,
        description: String?,
        startAt: Instant,
        endAt: Instant?,
        isAllDay: Boolean,
        color: String?,
    ) {
        val groupId = _uiState.value.selectedGroup?.id ?: return
        _uiState.update { it.copy(isCreatingEvent = true) }
        viewModelScope.launch {
            runCatching {
                createEventUseCase(
                    groupId,
                    CreateEventRequest(title, description, startAt, endAt, isAllDay, color),
                )
            }
                .onSuccess {
                    _uiState.update { it.copy(isCreatingEvent = false, showEventCreate = false) }
                    loadEvents()
                }
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.message, isCreatingEvent = false) }
                }
        }
    }

    fun updateEvent(
        eventId: String,
        title: String?,
        description: String?,
        startAt: Instant?,
        endAt: Instant?,
        isAllDay: Boolean?,
        color: String?,
    ) {
        val groupId = _uiState.value.selectedGroup?.id ?: return
        _uiState.update { it.copy(isCreatingEvent = true) }
        viewModelScope.launch {
            runCatching {
                updateEventUseCase(
                    groupId,
                    eventId,
                    UpdateEventRequest(title, description, startAt, endAt, isAllDay, color),
                )
            }
                .onSuccess {
                    _uiState.update { it.copy(isCreatingEvent = false, showEventCreate = false, editingEvent = null) }
                    loadEvents()
                }
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.message, isCreatingEvent = false) }
                }
        }
    }

    fun deleteEvent(eventId: String) {
        val groupId = _uiState.value.selectedGroup?.id ?: return
        viewModelScope.launch {
            runCatching { deleteEventUseCase(groupId, eventId) }
                .onSuccess {
                    _uiState.update { it.copy(showEventDetail = false, selectedEvent = null) }
                    loadEvents()
                }
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.message) }
                }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    private fun loadGroups() {
        viewModelScope.launch {
            runCatching { getMyGroupsUseCase() }
                .onSuccess { groups ->
                    _uiState.update {
                        it.copy(
                            groups = groups.toPersistentList(),
                            selectedGroup = groups.firstOrNull(),
                            isLoading = false,
                        )
                    }
                    loadEvents()
                }
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                }
        }
    }

    private fun loadEvents() {
        val state = _uiState.value
        val group = state.selectedGroup ?: return

        viewModelScope.launch {
            getMonthEventsUseCase(group.id, state.currentYear, state.currentMonth)
                .collect { events ->
                    _uiState.update { it.copy(events = events.toPersistentList()) }
                }
        }
    }

    private fun initialState(): CalendarUiState {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        return CalendarUiState(
            currentYear = now.year,
            currentMonth = now.monthNumber,
            selectedDate = LocalDate(now.year, now.monthNumber, now.dayOfMonth),
        )
    }
}
