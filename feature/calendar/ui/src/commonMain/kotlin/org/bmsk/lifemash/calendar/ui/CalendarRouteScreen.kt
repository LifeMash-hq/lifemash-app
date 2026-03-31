package org.bmsk.lifemash.calendar.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun CalendarRouteScreen(
    onShowErrorSnackbar: (Throwable?) -> Unit,
    viewModel: CalendarViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadGroups()
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            onShowErrorSnackbar(Exception(it))
            viewModel.clearError()
        }
    }

    CalendarScreen(
        uiState = uiState,
        onDateSelect = viewModel::selectDate,
        onChangeMonth = viewModel::changeMonth,
        onSelectGroup = viewModel::selectGroup,
        onShowOverlay = viewModel::showOverlay,
        onCreateGroup = viewModel::createGroup,
        onJoinGroup = viewModel::joinGroup,
    )

    val groupId = uiState.selectedGroup?.id

    when (val overlay = uiState.overlay) {
        is CalendarOverlay.EventCreate -> {
            if (groupId != null) {
                EventCreateBottomSheet(
                    editingEvent = null,
                    isLoading = uiState.isCreatingEvent,
                    selectedDate = overlay.selectedDate,
                    onDismiss = viewModel::dismissOverlay,
                    onSubmit = { form -> viewModel.createEvent(groupId, form) },
                )
            }
        }

        is CalendarOverlay.EventEdit -> {
            if (groupId != null) {
                EventCreateBottomSheet(
                    editingEvent = overlay.event,
                    isLoading = uiState.isCreatingEvent,
                    selectedDate = null,
                    onDismiss = viewModel::dismissOverlay,
                    onSubmit = { form -> viewModel.updateEvent(groupId, overlay.event.id, form) },
                )
            }
        }

        is CalendarOverlay.EventDetail -> {
            if (groupId != null) {
                EventDetailBottomSheet(
                    event = overlay.event,
                    onDismiss = viewModel::dismissOverlay,
                    onEdit = { viewModel.showOverlay(CalendarOverlay.EventEdit(overlay.event)) },
                    onDelete = { viewModel.deleteEvent(groupId, overlay.event.id) },
                )
            }
        }

        is CalendarOverlay.GroupRename -> {
            val group = uiState.selectedGroup
            if (group != null) {
                GroupRenameDialog(
                    currentName = group.name ?: "",
                    isLoading = uiState.isRenamingGroup,
                    onDismiss = viewModel::dismissOverlay,
                    onConfirm = { name -> viewModel.updateGroupName(group.id, name) },
                )
            }
        }

        CalendarOverlay.None -> {}
    }
}
