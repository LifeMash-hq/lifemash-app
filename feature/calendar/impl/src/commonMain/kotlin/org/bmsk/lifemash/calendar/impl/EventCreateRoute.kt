package org.bmsk.lifemash.calendar.impl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.bmsk.lifemash.domain.calendar.Event
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun EventCreateRoute(
    year: Int,
    month: Int,
    day: Int,
    groupId: String? = null,
    onBack: () -> Unit,
    onEventCreated: () -> Unit,
    viewModel: EventCreateViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.initForm(year, month, day, groupId, existingEvent = null)
    }

    LaunchedEffect(uiState.event) {
        if (uiState.event is EventCreateEvent.Saved) {
            viewModel.consumeEvent()
            onEventCreated()
        }
    }

    EventCreateScreen(
        uiState = uiState,
        onCancel = onBack,
        onSave = viewModel::save,
        onTitleChange = viewModel::updateTitle,
        onLocationChange = viewModel::updateLocation,
        onMemoChange = viewModel::updateMemo,
        onColorSelect = viewModel::selectColor,
        onVisibilitySelect = viewModel::selectVisibility,
        onDateTimeChange = viewModel::updateDateTime,
        onSwitchTab = viewModel::switchTab,
        onShowVisibilitySheet = viewModel::showVisibilitySheet,
        onDismissVisibilitySheet = viewModel::dismissVisibilitySheet,
        onConfirmLocation = viewModel::confirmLocation,
    )
}

@Composable
internal fun EventEditRoute(
    groupId: String,
    existingEvent: Event,
    onBack: () -> Unit,
    onEventUpdated: () -> Unit,
    viewModel: EventCreateViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.initForm(0, 0, 0, groupId, existingEvent)
    }

    LaunchedEffect(uiState.event) {
        if (uiState.event is EventCreateEvent.Saved) {
            viewModel.consumeEvent()
            onEventUpdated()
        }
    }

    EventCreateScreen(
        uiState = uiState,
        isEdit = true,
        onCancel = onBack,
        onSave = { viewModel.saveEdit(groupId, existingEvent.id) },
        onTitleChange = viewModel::updateTitle,
        onLocationChange = viewModel::updateLocation,
        onMemoChange = viewModel::updateMemo,
        onColorSelect = viewModel::selectColor,
        onVisibilitySelect = viewModel::selectVisibility,
        onDateTimeChange = viewModel::updateDateTime,
        onSwitchTab = viewModel::switchTab,
        onShowVisibilitySheet = viewModel::showVisibilitySheet,
        onDismissVisibilitySheet = viewModel::dismissVisibilitySheet,
        onConfirmLocation = viewModel::confirmLocation,
    )
}
