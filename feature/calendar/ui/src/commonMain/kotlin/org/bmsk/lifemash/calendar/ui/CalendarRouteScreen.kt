package org.bmsk.lifemash.calendar.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlin.time.Instant
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun CalendarRouteScreen(
    onShowErrorSnackbar: (Throwable?) -> Unit,
    viewModel: CalendarViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            onShowErrorSnackbar(Exception(it))
            viewModel.clearError()
        }
    }

    CalendarScreen(
        uiState = uiState,
        onDateSelect = viewModel::selectDate,
        onPrevMonth = {
            val (y, m) = if (uiState.currentMonth == 1) {
                uiState.currentYear - 1 to 12
            } else {
                uiState.currentYear to uiState.currentMonth - 1
            }
            viewModel.changeMonth(y, m)
        },
        onNextMonth = {
            val (y, m) = if (uiState.currentMonth == 12) {
                uiState.currentYear + 1 to 1
            } else {
                uiState.currentYear to uiState.currentMonth + 1
            }
            viewModel.changeMonth(y, m)
        },
        onCreateGroup = viewModel::createGroup,
        onJoinGroup = viewModel::joinGroup,
        onSelectGroup = viewModel::selectGroup,
        onShowGroupRename = viewModel::showGroupRenameDialog,
        onHideGroupRename = viewModel::hideGroupRenameDialog,
        onRenameGroup = { groupId, name -> viewModel.updateGroupName(groupId, name) },
        onShowEventCreate = viewModel::showEventCreate,
        onHideEventCreate = viewModel::hideEventCreate,
        onCreateEvent = { title, desc, startMs, endMs, isAllDay, color ->
            viewModel.createEvent(
                title = title,
                description = desc,
                startAt = Instant.fromEpochMilliseconds(startMs),
                endAt = endMs?.let { Instant.fromEpochMilliseconds(it) },
                isAllDay = isAllDay,
                color = color,
            )
        },
        onShowEventDetail = viewModel::showEventDetail,
        onHideEventDetail = viewModel::hideEventDetail,
        onStartEditEvent = viewModel::startEditEvent,
        onUpdateEvent = { eventId, title, desc, startMs, endMs, isAllDay, color ->
            viewModel.updateEvent(
                eventId = eventId,
                title = title,
                description = desc,
                startAt = startMs?.let { Instant.fromEpochMilliseconds(it) },
                endAt = endMs?.let { Instant.fromEpochMilliseconds(it) },
                isAllDay = isAllDay,
                color = color,
            )
        },
        onDeleteEvent = viewModel::deleteEvent,
    )
}
