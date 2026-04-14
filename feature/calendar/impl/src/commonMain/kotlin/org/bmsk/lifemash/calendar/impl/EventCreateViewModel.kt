package org.bmsk.lifemash.calendar.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import org.bmsk.lifemash.domain.calendar.Event
import org.bmsk.lifemash.domain.calendar.EventVisibility
import org.bmsk.lifemash.domain.usecase.calendar.CreateEventUseCase
import org.bmsk.lifemash.domain.usecase.calendar.GetMyGroupsUseCase
import org.bmsk.lifemash.domain.usecase.calendar.UpdateEventUseCase

internal class EventCreateViewModel(
    private val getMyGroups: GetMyGroupsUseCase,
    private val createEvent: CreateEventUseCase,
    private val updateEvent: UpdateEventUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(EventCreateUiState.Default)
    val uiState: StateFlow<EventCreateUiState> = _uiState.asStateFlow()

    private var resolvedGroupId: String? = null

    fun loadGroup(groupId: String?) {
        if (groupId != null) {
            resolvedGroupId = groupId
            return
        }
        viewModelScope.launch {
            runCatching {
                val groups = getMyGroups()
                resolvedGroupId = groups.firstOrNull()?.id
            }.onFailure { /* 그룹 없으면 무시 */ }
        }
    }

    fun createEvent(
        title: String,
        color: String?,
        dateTime: EventDateTime,
        location: String?,
        visibility: EventVisibility = EventVisibility.Followers,
        memo: String? = null,
    ) {
        val gId = resolvedGroupId ?: return
        _uiState.update { it.copy(isSaving = true) }

        val tz = TimeZone.currentSystemDefault()
        val startAt = dateTime.toStartInstant(tz)
        val endAt = dateTime.toEndInstant(tz)

        viewModelScope.launch {
            runCatching {
                createEvent(
                    groupId = gId,
                    title = title,
                    description = memo,
                    location = location,
                    startAt = startAt,
                    endAt = endAt,
                    isAllDay = dateTime.isAllDay,
                    color = color,
                    visibility = visibility,
                )
            }.onSuccess {
                _uiState.update { it.copy(isSaving = false, event = EventCreateEvent.Saved) }
            }.onFailure { e ->
                _uiState.update { it.copy(isSaving = false, errorMessage = e.message ?: "일정 생성 실패") }
            }
        }
    }

    fun updateEvent(
        groupId: String,
        event: Event,
        title: String,
        color: String?,
        dateTime: EventDateTime,
        location: String?,
        visibility: EventVisibility = EventVisibility.Followers,
        memo: String? = null,
    ) {
        _uiState.update { it.copy(isSaving = true) }

        val tz = TimeZone.currentSystemDefault()
        val startAt = dateTime.toStartInstant(tz)
        val endAt = dateTime.toEndInstant(tz)

        viewModelScope.launch {
            runCatching {
                updateEvent(
                    groupId = groupId,
                    eventId = event.id,
                    title = title,
                    description = memo,
                    location = location,
                    startAt = startAt,
                    endAt = endAt,
                    isAllDay = dateTime.isAllDay,
                    color = color,
                    visibility = visibility,
                )
            }.onSuccess {
                _uiState.update { it.copy(isSaving = false, event = EventCreateEvent.Saved) }
            }.onFailure { e ->
                _uiState.update { it.copy(isSaving = false, errorMessage = e.message ?: "일정 수정 실패") }
            }
        }
    }

    fun consumeEvent() {
        _uiState.update { it.copy(event = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
