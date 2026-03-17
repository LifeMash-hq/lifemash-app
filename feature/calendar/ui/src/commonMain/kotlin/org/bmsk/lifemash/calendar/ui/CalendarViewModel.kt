package org.bmsk.lifemash.calendar.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.bmsk.lifemash.calendar.domain.usecase.GetMonthEventsUseCase
import org.bmsk.lifemash.calendar.domain.usecase.GetMyGroupsUseCase

internal class CalendarViewModel(
    private val getMonthEventsUseCase: GetMonthEventsUseCase,
    private val getMyGroupsUseCase: GetMyGroupsUseCase,
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
