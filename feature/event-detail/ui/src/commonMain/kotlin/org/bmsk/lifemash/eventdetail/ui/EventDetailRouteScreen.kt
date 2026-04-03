package org.bmsk.lifemash.eventdetail.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.bmsk.lifemash.feature.shared.common.rememberAddToCalendarLauncher
import org.bmsk.lifemash.feature.shared.common.rememberShareLauncher
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun EventDetailRouteScreen(
    eventId: String,
    onBack: () -> Unit,
    viewModel: EventDetailViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val shareLauncher = rememberShareLauncher()
    val addToCalendarLauncher = rememberAddToCalendarLauncher()

    LaunchedEffect(eventId) {
        viewModel.loadEvent(eventId)
    }

    EventDetailScreen(
        uiState = uiState,
        onBack = onBack,
        onJoinToggle = viewModel::toggleJoin,
        onAddComment = { content ->
            (uiState as? EventDetailUiState.Loaded)?.let { viewModel.addComment(it, content) }
        },
        onShareClick = {
            (uiState as? EventDetailUiState.Loaded)?.let { loaded ->
                val text = buildString {
                    append(loaded.title)
                    append("\n")
                    append(loaded.date)
                    loaded.location?.let { append("\n$it") }
                }
                shareLauncher(text)
            }
        },
        onCalendarAddClick = {
            (uiState as? EventDetailUiState.Loaded)?.let { loaded ->
                addToCalendarLauncher(
                    loaded.title,
                    loaded.startAt.toEpochMilliseconds(),
                    loaded.endAt?.toEpochMilliseconds(),
                    loaded.location,
                    loaded.description,
                )
            }
        },
    )
}
