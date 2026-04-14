package org.bmsk.lifemash.moment.impl

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import org.bmsk.lifemash.domain.calendar.Event
import org.bmsk.lifemash.designsystem.theme.LifeMashSpacing
import androidx.compose.foundation.Canvas

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EventSelectSheet(
    viewModel: PostMomentViewModel,
    onDismiss: () -> Unit,
) {
    val events by viewModel.events.collectAsStateWithLifecycle()
    val form by viewModel.form.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(modifier = Modifier.navigationBarsPadding()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = LifeMashSpacing.lg, vertical = LifeMashSpacing.sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "일정 선택",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                )
                if (form.eventId != null) {
                    TextButton(onClick = {
                        viewModel.onTagEvent(null, null)
                        onDismiss()
                    }) {
                        Text("선택 해제", color = MaterialTheme.colorScheme.error)
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Outlined.Close, contentDescription = "닫기")
                }
            }
            HorizontalDivider()
            if (events.isEmpty()) {
                Text(
                    text = "태그할 일정이 없습니다",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(LifeMashSpacing.xl),
                )
            } else {
                LazyColumn {
                    items(events) { event ->
                        EventSelectItem(
                            event = event,
                            isSelected = event.id == form.eventId,
                            onClick = {
                                viewModel.onTagEvent(event.id, event.title)
                                onDismiss()
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EventSelectItem(
    event: Event,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val tz = TimeZone.currentSystemDefault()
    val startLocal = event.startAt.toLocalDateTime(tz)
    val dateLabel = "${startLocal.month.number}월 ${startLocal.day}일"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = LifeMashSpacing.lg, vertical = LifeMashSpacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val dotColor = event.color
            ?.let { hex ->
                runCatching {
                    Color(("FF" + hex.trimStart('#')).toLong(16) or 0xFF000000L)
                }.getOrNull()
            }
            ?: MaterialTheme.colorScheme.primary

        Canvas(
            modifier = Modifier
                .size(LifeMashSpacing.md)
                .clip(CircleShape),
        ) {
            drawCircle(color = dotColor)
        }
        Spacer(modifier = Modifier.width(LifeMashSpacing.md))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = event.title,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = dateLabel,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
    HorizontalDivider(modifier = Modifier.padding(start = LifeMashSpacing.xxxl))
}