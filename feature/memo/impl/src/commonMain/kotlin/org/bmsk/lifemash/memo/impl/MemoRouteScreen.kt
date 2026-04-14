package org.bmsk.lifemash.memo.impl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun MemoRouteScreen(
    onShowErrorSnackbar: (Throwable?) -> Unit,
    onBack: () -> Unit = {},
    viewModel: MemoViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadMemos()
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            onShowErrorSnackbar(Exception(it))
            viewModel.clearError()
        }
    }

    MemoScreen(
        uiState = uiState,
        onBack = onBack,
        onSearchQueryChange = viewModel::search,
        onShowOverlay = viewModel::showOverlay,
    )

    when (val overlay = uiState.overlay) {
        is MemoOverlay.Create -> {
            MemoCreateBottomSheet(
                editingMemo = null,
                isLoading = uiState.isCreating,
                onDismiss = viewModel::dismissOverlay,
                onSubmit = { title, content, isPinned, isChecklist ->
                    viewModel.createMemo(title, content, isPinned, isChecklist)
                },
            )
        }

        is MemoOverlay.Edit -> {
            MemoCreateBottomSheet(
                editingMemo = overlay.memo,
                isLoading = uiState.isUpdating,
                onDismiss = viewModel::dismissOverlay,
                onSubmit = { title, content, isPinned, _ ->
                    viewModel.updateMemo(overlay.memo.id, title, content, isPinned)
                },
            )
        }

        is MemoOverlay.Detail -> {
            MemoDetailBottomSheet(
                memo = overlay.memo,
                onDismiss = viewModel::dismissOverlay,
                onEdit = { viewModel.showOverlay(MemoOverlay.Edit(overlay.memo)) },
                onDelete = { viewModel.deleteMemo(overlay.memo.id) },
                onToggleChecklistItem = { itemId -> viewModel.toggleChecklistItem(overlay.memo.id, itemId) },
            )
        }

        MemoOverlay.None -> {}
    }
}
