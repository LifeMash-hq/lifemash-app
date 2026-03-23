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
import org.bmsk.lifemash.calendar.domain.repository.CreateEventRequest
import org.bmsk.lifemash.calendar.domain.repository.UpdateEventRequest
import org.bmsk.lifemash.calendar.domain.usecase.CreateEventUseCase
import org.bmsk.lifemash.calendar.domain.usecase.CreateGroupUseCase
import org.bmsk.lifemash.calendar.domain.usecase.DeleteEventUseCase
import org.bmsk.lifemash.calendar.domain.usecase.GetMonthEventsUseCase
import org.bmsk.lifemash.calendar.domain.usecase.GetMyGroupsUseCase
import org.bmsk.lifemash.calendar.domain.usecase.JoinGroupUseCase
import org.bmsk.lifemash.calendar.domain.usecase.UpdateEventUseCase
import org.bmsk.lifemash.calendar.domain.usecase.UpdateGroupNameUseCase

internal class CalendarViewModel(
    private val getMonthEventsUseCase: GetMonthEventsUseCase,
    private val getMyGroupsUseCase: GetMyGroupsUseCase,
    private val createGroupUseCase: CreateGroupUseCase,
    private val joinGroupUseCase: JoinGroupUseCase,
    private val createEventUseCase: CreateEventUseCase,
    private val updateEventUseCase: UpdateEventUseCase,
    private val deleteEventUseCase: DeleteEventUseCase,
    private val updateGroupNameUseCase: UpdateGroupNameUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<CalendarUiState>(CalendarUiState.Loading)
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    fun selectDate(date: LocalDate) {
        updateLoaded { copy(selectedDate = date) }
    }

    fun changeMonth(year: Int, month: Int) {
        updateLoaded { copy(currentYear = year, currentMonth = month) }
        val groupId = (_uiState.value as? CalendarUiState.Loaded)?.selectedGroup?.id ?: return
        loadEvents(groupId, year, month)
    }

    fun selectGroup(groupId: String) {
        updateLoaded {
            val group = groups.find { it.id == groupId }
            copy(selectedGroup = group)
        }
        val loaded = _uiState.value as? CalendarUiState.Loaded ?: return
        loadEvents(groupId, loaded.currentYear, loaded.currentMonth)
    }

    fun createGroup(type: GroupType = GroupType.COUPLE, name: String? = null) {
        updateLoaded { copy(isCreatingGroup = true) }
        viewModelScope.launch {
            runCatching { createGroupUseCase(type, name) }
                .onSuccess { group ->
                    updateLoaded {
                        val updatedGroups = (groups.toList() + group).toPersistentList()
                        copy(
                            groups = updatedGroups,
                            selectedGroup = group,
                            isCreatingGroup = false,
                        )
                    }
                    loadEvents()
                }
                .onFailure { e ->
                    updateLoaded { copy(isCreatingGroup = false) }
                    _uiState.value = CalendarUiState.Error(e.message ?: "그룹 생성 실패")
                }
        }
    }

    fun joinGroup(inviteCode: String) {
        updateLoaded { copy(isCreatingGroup = true) }
        viewModelScope.launch {
            runCatching { joinGroupUseCase(inviteCode) }
                .onSuccess { group ->
                    updateLoaded {
                        val updatedGroups = (groups.toList() + group).toPersistentList()
                        copy(
                            groups = updatedGroups,
                            selectedGroup = group,
                            isCreatingGroup = false,
                        )
                    }
                    loadEvents()
                }
                .onFailure { e ->
                    updateLoaded { copy(isCreatingGroup = false) }
                    _uiState.value = CalendarUiState.Error(e.message ?: "그룹 참여 실패")
                }
        }
    }

    fun showOverlay(overlay: CalendarOverlay) {
        updateLoaded { copy(overlay = overlay) }
    }

    fun dismissOverlay() {
        updateLoaded { copy(overlay = CalendarOverlay.None) }
    }

    fun createEvent(groupId: String, form: EventFormData) {
        updateLoaded { copy(isCreatingEvent = true) }
        viewModelScope.launch {
            runCatching {
                createEventUseCase(
                    groupId,
                    CreateEventRequest(
                        title = form.title,
                        description = form.description,
                        startAt = Instant.fromEpochMilliseconds(form.startAt),
                        endAt = form.endAt?.let { Instant.fromEpochMilliseconds(it) },
                        isAllDay = form.isAllDay,
                        color = form.color,
                    ),
                )
            }
                .onSuccess {
                    updateLoaded { copy(isCreatingEvent = false, overlay = CalendarOverlay.None) }
                    loadEvents()
                }
                .onFailure { e ->
                    updateLoaded { copy(isCreatingEvent = false) }
                    _uiState.value = CalendarUiState.Error(e.message ?: "일정 생성 실패")
                }
        }
    }

    fun updateEvent(groupId: String, eventId: String, form: EventFormData) {
        updateLoaded { copy(isCreatingEvent = true) }
        viewModelScope.launch {
            runCatching {
                updateEventUseCase(
                    groupId,
                    eventId,
                    UpdateEventRequest(
                        title = form.title,
                        description = form.description,
                        startAt = Instant.fromEpochMilliseconds(form.startAt),
                        endAt = form.endAt?.let { Instant.fromEpochMilliseconds(it) },
                        isAllDay = form.isAllDay,
                        color = form.color,
                    ),
                )
            }
                .onSuccess {
                    updateLoaded { copy(isCreatingEvent = false, overlay = CalendarOverlay.None) }
                    loadEvents()
                }
                .onFailure { e ->
                    updateLoaded { copy(isCreatingEvent = false) }
                    _uiState.value = CalendarUiState.Error(e.message ?: "일정 수정 실패")
                }
        }
    }

    fun deleteEvent(groupId: String, eventId: String) {
        viewModelScope.launch {
            runCatching { deleteEventUseCase(groupId, eventId) }
                .onSuccess {
                    updateLoaded { copy(overlay = CalendarOverlay.None) }
                    loadEvents()
                }
                .onFailure { e ->
                    _uiState.value = CalendarUiState.Error(e.message ?: "일정 삭제 실패")
                }
        }
    }

    fun updateGroupName(groupId: String, name: String) {
        updateLoaded { copy(isRenamingGroup = true) }
        viewModelScope.launch {
            runCatching { updateGroupNameUseCase(groupId, name) }
                .onSuccess { updatedGroup ->
                    updateLoaded {
                        val updatedGroups = groups.map {
                            if (it.id == updatedGroup.id) updatedGroup else it
                        }.toPersistentList()
                        copy(
                            groups = updatedGroups,
                            selectedGroup = updatedGroup,
                            overlay = CalendarOverlay.None,
                            isRenamingGroup = false,
                        )
                    }
                }
                .onFailure { e ->
                    updateLoaded { copy(isRenamingGroup = false) }
                    _uiState.value = CalendarUiState.Error(e.message ?: "그룹명 변경 실패")
                }
        }
    }

    internal fun loadGroups() {
        viewModelScope.launch {
            runCatching { getMyGroupsUseCase() }
                .onSuccess { groups ->
                    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                    _uiState.value = CalendarUiState.Loaded(
                        currentYear = now.year,
                        currentMonth = now.monthNumber,
                        selectedDate = LocalDate(now.year, now.monthNumber, now.dayOfMonth),
                        groups = groups.toPersistentList(),
                        selectedGroup = groups.firstOrNull(),
                    )
                    loadEvents()
                }
                .onFailure { e ->
                    _uiState.value = CalendarUiState.Error(e.message ?: "그룹 로드 실패")
                }
        }
    }

    private fun loadEvents() {
        val loaded = _uiState.value as? CalendarUiState.Loaded ?: return
        val group = loaded.selectedGroup ?: return
        loadEvents(group.id, loaded.currentYear, loaded.currentMonth)
    }

    private fun loadEvents(groupId: String, year: Int, month: Int) {
        viewModelScope.launch {
            getMonthEventsUseCase(groupId, year, month)
                .collect { events ->
                    updateLoaded { copy(events = events.toPersistentList()) }
                }
        }
    }

    private fun updateLoaded(transform: CalendarUiState.Loaded.() -> CalendarUiState.Loaded) {
        _uiState.update { state ->
            when (state) {
                is CalendarUiState.Loaded -> state.transform()
                else -> state
            }
        }
    }
}
