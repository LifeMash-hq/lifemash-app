package org.bmsk.lifemash.calendar.impl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import org.bmsk.lifemash.domain.calendar.Event
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun CalendarRouteScreen(
    onShowErrorSnackbar: (Throwable?) -> Unit,
    onBack: () -> Unit = {},
    onNavigateToEventCreate: (year: Int, month: Int, day: Int, groupId: String?) -> Unit = { _, _, _, _ -> },
    onNavigateToEventEdit: (groupId: String, event: Event) -> Unit = { _, _ -> },
    navController: NavController? = null,
    viewModel: CalendarViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadGroups()
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            onShowErrorSnackbar(Exception(it))
            viewModel.clearError()
        }
    }

    // 일정 생성/수정 후 돌아왔을 때 목록 새로고침
    LaunchedEffect(navController) {
        navController?.currentBackStackEntry
            ?.savedStateHandle
            ?.getStateFlow("event_changed", false)
            ?.collect { changed ->
                if (changed) {
                    viewModel.refreshEvents()
                    navController.currentBackStackEntry
                        ?.savedStateHandle?.set("event_changed", false)
                }
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
        onNavigateToEventCreate = onNavigateToEventCreate,
        onNavigateToEventEdit = onNavigateToEventEdit,
        onBack = onBack,
    )

    val groupId = uiState.selectedGroup?.id

    when (val overlay = uiState.overlay) {
        is CalendarOverlay.EventDetail -> {
            if (groupId != null) {
                EventDetailBottomSheet(
                    event = overlay.event,
                    onDismiss = viewModel::dismissOverlay,
                    onEdit = { event -> onNavigateToEventEdit(groupId, event) },
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