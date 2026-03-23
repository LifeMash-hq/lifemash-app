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

    LaunchedEffect(uiState) {
        val error = (uiState as? CalendarUiState.Error)?.message
        if (error != null) {
            onShowErrorSnackbar(Exception(error))
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

    val loaded = uiState as? CalendarUiState.Loaded ?: return
    val groupId = loaded.selectedGroup?.id

    when (val overlay = loaded.overlay) {
        is CalendarOverlay.EventCreate -> {
            if (groupId != null) {
                EventCreateBottomSheet(
                    editingEvent = null,
                    isLoading = loaded.isCreatingEvent,
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
                    isLoading = loaded.isCreatingEvent,
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
            val group = loaded.selectedGroup
            if (group != null) {
                GroupRenameDialog(
                    currentName = group.name ?: "",
                    isLoading = loaded.isRenamingGroup,
                    onDismiss = viewModel::dismissOverlay,
                    onConfirm = { name -> viewModel.updateGroupName(group.id, name) },
                )
            }
        }

        CalendarOverlay.None -> {}
    }
}
