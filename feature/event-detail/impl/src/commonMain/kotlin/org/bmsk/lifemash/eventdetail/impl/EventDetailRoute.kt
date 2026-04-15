@file:OptIn(kotlin.time.ExperimentalTime::class)
package org.bmsk.lifemash.eventdetail.impl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import org.bmsk.lifemash.domain.calendar.EventTiming
import org.bmsk.lifemash.feature.shared.common.rememberAddToCalendarLauncher
import org.bmsk.lifemash.feature.shared.common.rememberShareLauncher
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun EventDetailRoute(
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
                val (startMs, endMs) = when (val t = loaded.timing) {
                    is EventTiming.AllDay ->
                        t.date.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds() to null
                    is EventTiming.Timed ->
                        t.start.toEpochMilliseconds() to t.end.toEpochMilliseconds()
                }
                addToCalendarLauncher(
                    loaded.title,
                    startMs,
                    endMs,
                    loaded.location,
                    loaded.description,
                )
            }
        },
    )
}
