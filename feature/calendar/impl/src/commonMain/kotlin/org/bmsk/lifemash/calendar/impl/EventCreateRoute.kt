package org.bmsk.lifemash.calendar.impl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
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
        viewModel.loadGroup(groupId)
    }

    LaunchedEffect(uiState.event) {
        if (uiState.event is EventCreateEvent.Saved) {
            viewModel.consumeEvent()
            onEventCreated()
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
            )
        },
        onCancel = onBack,
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

    LaunchedEffect(uiState.event) {
        if (uiState.event is EventCreateEvent.Saved) {
            viewModel.consumeEvent()
            onEventUpdated()
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
            )
        },
        onCancel = onBack,
    )
}
