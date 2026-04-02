package org.bmsk.lifemash.calendar.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import org.bmsk.lifemash.calendar.domain.model.Event
import org.bmsk.lifemash.calendar.domain.model.EventVisibility
import org.bmsk.lifemash.calendar.domain.repository.EventRepository
import org.bmsk.lifemash.calendar.domain.repository.GroupRepository

data class EventCreateUiState(
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
)

internal class EventCreateViewModel(
    private val eventRepository: EventRepository,
    private val groupRepository: GroupRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(EventCreateUiState())
    val uiState: StateFlow<EventCreateUiState> = _uiState

    private var resolvedGroupId: String? = null

    fun loadGroup(groupId: String?) {
        if (groupId != null) {
            resolvedGroupId = groupId
            return
        }
        viewModelScope.launch {
            runCatching {
                val groups = groupRepository.getMyGroups()
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
        onDone: () -> Unit,
    ) {
        val gId = resolvedGroupId ?: return
        _uiState.update { it.copy(isSaving = true) }

        val tz = TimeZone.currentSystemDefault()
        val startAt = dateTime.toStartInstant(tz)
        val endAt = dateTime.toEndInstant(tz)

        viewModelScope.launch {
            runCatching {
                eventRepository.createEvent(
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
                _uiState.update { it.copy(isSaving = false) }
                onDone()
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
        onDone: () -> Unit,
    ) {
        _uiState.update { it.copy(isSaving = true) }

        val tz = TimeZone.currentSystemDefault()
        val startAt = dateTime.toStartInstant(tz)
        val endAt = dateTime.toEndInstant(tz)

        viewModelScope.launch {
            runCatching {
                eventRepository.updateEvent(
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
                _uiState.update { it.copy(isSaving = false) }
                onDone()
            }.onFailure { e ->
                _uiState.update { it.copy(isSaving = false, errorMessage = e.message ?: "일정 수정 실패") }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
