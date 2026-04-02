package org.bmsk.lifemash.calendar.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import org.bmsk.lifemash.calendar.domain.model.Event
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun EventCreateRouteScreen(
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
        viewModel.loadGroup(groupId)
    }

    LaunchedEffect(uiState.errorMessage) {
        if (uiState.errorMessage != null) {
            viewModel.clearError()
        }
    }

    EventCreateScreen(
        uiState = uiState,
        year = year,
        month = month,
        day = day,
        existingEvent = null,
        onSave = { title, color, dateTime, location, visibility, memo ->
            viewModel.createEvent(
                title = title,
                color = color,
                dateTime = dateTime,
                location = location,
                visibility = visibility,
                memo = memo,
                onDone = onEventCreated,
            )
        },
        onCancel = onBack,
    )
}

@Composable
internal fun EventEditRouteScreen(
    groupId: String,
    existingEvent: Event,
    onBack: () -> Unit,
    onEventUpdated: () -> Unit,
    viewModel: EventCreateViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.errorMessage) {
        if (uiState.errorMessage != null) {
            viewModel.clearError()
        }
    }

    EventCreateScreen(
        uiState = uiState,
        year = 0,
        month = 0,
        day = 0,
        existingEvent = existingEvent,
        onSave = { title, color, dateTime, location, visibility, memo ->
            viewModel.updateEvent(
                groupId = groupId,
                event = existingEvent,
                title = title,
                color = color,
                dateTime = dateTime,
                location = location,
                visibility = visibility,
                memo = memo,
                onDone = onEventUpdated,
            )
        },
        onCancel = onBack,
    )
}
