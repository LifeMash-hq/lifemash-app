package org.bmsk.lifemash.eventdetail.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun EventDetailRouteScreen(
    eventId: String,
    onBack: () -> Unit,
    viewModel: EventDetailViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(eventId) {
        viewModel.loadEvent(eventId)
    }

    EventDetailScreen(
        uiState = uiState,
        onBack = onBack,
        onJoinToggle = viewModel::toggleJoin,
    )
}
