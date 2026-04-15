package org.bmsk.lifemash.calendar.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.bmsk.lifemash.domain.calendar.Event
import org.bmsk.lifemash.domain.calendar.EventTiming
import org.bmsk.lifemash.domain.calendar.EventVisibility
import org.bmsk.lifemash.domain.usecase.calendar.CreateEventUseCase
import org.bmsk.lifemash.domain.usecase.calendar.GetMyGroupsUseCase
import org.bmsk.lifemash.domain.usecase.calendar.UpdateEventUseCase

internal class EventCreateViewModel(
    private val getMyGroupsUseCase: GetMyGroupsUseCase,
    private val createEventUseCase: CreateEventUseCase,
    private val updateEventUseCase: UpdateEventUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(EventCreateUiState.Default)
    val uiState: StateFlow<EventCreateUiState> = _uiState.asStateFlow()

    private var resolvedGroupId: String? = null

    fun initForm(year: Int, month: Int, day: Int, groupId: String?, existingEvent: Event?) {
        if (groupId != null) {
            resolvedGroupId = groupId
        } else {
            viewModelScope.launch {
                runCatching { getMyGroupsUseCase() }
                    .onSuccess { groups -> resolvedGroupId = groups.firstOrNull()?.id }
            }
        }

        if (existingEvent != null) {
            val tz = TimeZone.currentSystemDefault()
            val eventDateTime = when (val timing = existingEvent.timing) {
                is EventTiming.AllDay -> EventDateTime(date = timing.date)
                is EventTiming.Timed -> {
                    val startLocal = timing.start.toLocalDateTime(tz)
                    val endLocal = timing.end.toLocalDateTime(tz)
                    EventDateTime(
                        date = startLocal.date,
                        startTime = TimeOfDay(startLocal.hour, startLocal.minute),
                        endTime = TimeOfDay(endLocal.hour, endLocal.minute),
                    )
                }
            }
            _uiState.update {
                it.copy(
                    title = existingEvent.title,
                    location = existingEvent.location ?: "",
                    selectedColor = existingEvent.color,
                    visibility = existingEvent.visibility,
                    memo = existingEvent.description ?: "",
                    eventDateTime = eventDateTime,
                )
            }
        } else if (day > 0) {
            _uiState.update { it.copy(eventDateTime = EventDateTime.of(year, month, day)) }
        }
    }

    // ─── 탭 전환 ────────────────────────────────────────────────────────────

    fun switchTab(tab: EventCreateTab) {
        _uiState.update { it.copy(activeTab = tab) }
    }

    fun showVisibilitySheet() {
        _uiState.update { it.copy(isVisibilitySheetVisible = true) }
    }

    fun dismissVisibilitySheet() {
        _uiState.update { it.copy(isVisibilitySheetVisible = false) }
    }

    // ─── 폼 상태 업데이트 ───────────────────────────────────────────────────

    fun updateTitle(value: String) {
        _uiState.update { it.withTitle(value) }
    }

    fun updateLocation(value: String) {
        _uiState.update { it.withLocation(value) }
    }

    fun updateMemo(value: String) {
        _uiState.update { it.withMemo(value) }
    }

    fun selectColor(hex: String?) {
        _uiState.update { it.copy(selectedColor = hex) }
    }

    fun selectVisibility(visibility: EventVisibility) {
        _uiState.update { it.copy(visibility = visibility, isVisibilitySheetVisible = false) }
    }

    fun updateDateTime(dateTime: EventDateTime) {
        _uiState.update { it.copy(eventDateTime = dateTime, activeTab = EventCreateTab.FORM) }
    }

    fun confirmLocation() {
        _uiState.update { it.copy(activeTab = EventCreateTab.FORM) }
    }

    // ─── 저장 ───────────────────────────────────────────────────────────────

    fun save() {
        if (!_uiState.value.isSaveEnabled) return
        val state = _uiState.value
        val gId = resolvedGroupId ?: return

        _uiState.update { it.copy(isSaving = true) }

        val tz = TimeZone.currentSystemDefault()
        val timing = state.eventDateTime.toTiming(tz)

        viewModelScope.launch {
            runCatching {
                createEventUseCase(
                    groupId = gId,
                    title = state.title,
                    description = state.memo.ifBlank { null },
                    location = state.location.ifBlank { null },
                    timing = timing,
                    color = state.selectedColor,
                    visibility = state.visibility,
                )
            }.onSuccess {
                _uiState.update { it.copy(isSaving = false, event = EventCreateEvent.Saved) }
            }.onFailure { e ->
                _uiState.update { it.copy(isSaving = false, errorMessage = e.message ?: "일정 생성 실패") }
            }
        }
    }

    fun saveEdit(groupId: String, eventId: String) {
        val state = _uiState.value
        _uiState.update { it.copy(isSaving = true) }

        val tz = TimeZone.currentSystemDefault()
        val timing = state.eventDateTime.toTiming(tz)

        viewModelScope.launch {
            runCatching {
                updateEventUseCase(
                    groupId = groupId,
                    eventId = eventId,
                    title = state.title,
                    description = state.memo.ifBlank { null },
                    location = state.location.ifBlank { null },
                    timing = timing,
                    color = state.selectedColor,
                    visibility = state.visibility,
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
