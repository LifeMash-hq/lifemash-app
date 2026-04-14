@file:OptIn(kotlin.time.ExperimentalTime::class)
package org.bmsk.lifemash.eventdetail.impl

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Notes
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.time.Clock
import kotlin.time.Instant
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import org.bmsk.lifemash.designsystem.component.AvatarSize
import org.bmsk.lifemash.designsystem.component.LifeMashAvatar
import org.bmsk.lifemash.designsystem.component.LifeMashButton
import org.bmsk.lifemash.designsystem.theme.LifeMashRadius
import org.bmsk.lifemash.designsystem.theme.LifeMashSpacing

@Composable
internal fun EventDetailScreen(
    uiState: EventDetailUiState,
    onBack: () -> Unit = {},
    onJoinToggle: (EventDetailUiState.Loaded) -> Unit = {},
    onAddComment: (String) -> Unit = {},
    onShareClick: () -> Unit = {},
    onCalendarAddClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing
    ) { padding ->
        when (uiState) {
            is EventDetailUiState.Loading -> {
                Box(
                    Modifier.padding(padding).fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) { CircularProgressIndicator() }
            }

            is EventDetailUiState.Error -> {
                Box(
                    Modifier.padding(padding).fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) { Text(uiState.message) }
            }

            is EventDetailUiState.Loaded -> {
                LoadedContent(
                    modifier = Modifier.padding(padding),
                    state = uiState,
                    onBack = onBack,
                    onJoinToggle = onJoinToggle,
                    onAddComment = onAddComment,
                    onShareClick = onShareClick,
                    onCalendarAddClick = onCalendarAddClick,
                )
            }
        }
    }
}

@Composable
private fun LoadedContent(
    state: EventDetailUiState.Loaded,
    onBack: () -> Unit,
    onJoinToggle: (EventDetailUiState.Loaded) -> Unit,
    onAddComment: (String) -> Unit,
    onShareClick: () -> Unit = {},
    onCalendarAddClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var commentText by remember { mutableStateOf("") }

    Column(modifier.fillMaxSize().imePadding()) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = LifeMashSpacing.xxs, vertical = LifeMashSpacing.xxs),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로")
            }
            Text(
                text = "일정 상세",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
            )
            IconButton(onClick = onCalendarAddClick) {
                Icon(Icons.Outlined.CalendarMonth, contentDescription = "캘린더 추가")
            }
            IconButton(onClick = onShareClick) {
                Icon(Icons.Outlined.Share, contentDescription = "공유")
            }
        }

        // Scrollable content
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
        ) {
            // Hero
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = state.imageEmoji, fontSize = 80.sp)
                // Gradient overlay at the bottom
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.55f)),
                            ),
                        )
                        .padding(horizontal = LifeMashSpacing.lg, vertical = LifeMashSpacing.md),
                    contentAlignment = Alignment.BottomStart,
                ) {
                    Column {
                        Text(
                            text = state.title,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                        state.sharedByNickname?.let { nickname ->
                            Text(
                                text = "$nickname 님이 공유",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f),
                            )
                        }
                    }
                }
            }

            // Detail section
            Column(modifier = Modifier.padding(horizontal = LifeMashSpacing.lg, vertical = LifeMashSpacing.md)) {
                DetailRow(
                    icon = {
                        Icon(
                            Icons.Outlined.CalendarMonth,
                            contentDescription = null,
                            modifier = Modifier.size(LifeMashSpacing.lg),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                    title = formatDateLine(state.startAt),
                    subtitle = formatTimeRange(state.startAt, state.endAt),
                )
                state.location?.let { location ->
                    Spacer(Modifier.height(LifeMashSpacing.lg))
                    val (locName, locAddr) = splitLocation(location)
                    DetailRow(
                        icon = {
                            Icon(
                                Icons.Outlined.LocationOn,
                                contentDescription = null,
                                modifier = Modifier.size(LifeMashSpacing.lg),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        },
                        title = locName,
                        subtitle = locAddr,
                    )
                }
                state.description?.let { desc ->
                    Spacer(Modifier.height(LifeMashSpacing.lg))
                    DetailRow(
                        icon = {
                            Icon(
                                Icons.AutoMirrored.Outlined.Notes,
                                contentDescription = null,
                                modifier = Modifier.size(LifeMashSpacing.lg),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        },
                        title = "메모",
                        subtitle = desc,
                    )
                }
            }

            // Attendees section
            if (state.attendees.isNotEmpty()) {
                Column(modifier = Modifier.padding(horizontal = LifeMashSpacing.lg)) {
                    Text(
                        text = "함께하는 사람 ${state.attendees.size}명",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    )
                    Spacer(Modifier.height(LifeMashSpacing.sm))
                    AttendeesRow(attendees = state.attendees)
                }
                Spacer(Modifier.height(LifeMashSpacing.lg))
            }

            // Comments section
            if (state.comments.isNotEmpty()) {
                Column(modifier = Modifier.padding(horizontal = LifeMashSpacing.lg)) {
                    Text(
                        text = "댓글 ${state.comments.size}",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    )
                    Spacer(Modifier.height(LifeMashSpacing.sm))
                    state.comments.forEach { comment ->
                        CommentItem(comment = comment)
                        Spacer(Modifier.height(LifeMashSpacing.sm))
                    }
                }
            }

            Spacer(Modifier.height(LifeMashSpacing.lg))
        }

        // Sticky bottom: join button + comment input
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = LifeMashSpacing.lg, vertical = LifeMashSpacing.md),
            verticalArrangement = Arrangement.spacedBy(LifeMashSpacing.sm),
        ) {
            LifeMashButton(
                text = if (state.isJoined) "참여 중" else "참여",
                onClick = { onJoinToggle(state) },
                modifier = Modifier.fillMaxWidth(),
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(LifeMashSpacing.sm),
            ) {
                LifeMashAvatar(
                    imageUrl = null,
                    name = "나",
                    size = AvatarSize.Small,
                )
                OutlinedTextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    placeholder = { Text("댓글 달기...", style = MaterialTheme.typography.bodySmall) },
                    textStyle = MaterialTheme.typography.bodySmall,
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                )
                IconButton(
                    onClick = {
                        val text = commentText.trim()
                        if (text.isNotEmpty()) {
                            onAddComment(text)
                            commentText = ""
                        }
                    },
                    enabled = commentText.isNotBlank(),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "전송",
                        tint = if (commentText.isNotBlank()) MaterialTheme.colorScheme.primary
                               else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailRow(
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String? = null,
) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .size(LifeMashSpacing.xxxl)
                .clip(RoundedCornerShape(LifeMashRadius.md))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            icon()
        }
        Spacer(Modifier.width(LifeMashSpacing.md))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
            )
            if (subtitle != null) {
                Spacer(Modifier.height(LifeMashSpacing.micro))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun AttendeesRow(attendees: List<Attendee>) {
    val display = attendees.take(5)
    val overflow = attendees.size - display.size

    Row(verticalAlignment = Alignment.CenterVertically) {
        display.forEachIndexed { index, attendee ->
            Box(modifier = Modifier.offset(x = (-8 * index).dp)) {
                LifeMashAvatar(
                    imageUrl = attendee.profileImage,
                    name = attendee.nickname,
                    size = AvatarSize.Small,
                )
            }
        }
        if (overflow > 0) {
            Box(
                modifier = Modifier
                    .offset(x = (-8 * display.size).dp)
                    .size(LifeMashSpacing.xxxl)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "+$overflow",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun CommentItem(comment: Comment) {
    Row(verticalAlignment = Alignment.Top) {
        LifeMashAvatar(
            imageUrl = null,
            name = comment.authorNickname,
            size = AvatarSize.Small,
        )
        Spacer(Modifier.width(LifeMashSpacing.sm))
        Column {
            Text(
                text = comment.authorNickname,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            )
            Spacer(Modifier.height(LifeMashSpacing.micro))
            Text(
                text = comment.content,
                style = MaterialTheme.typography.bodySmall,
            )
            Spacer(Modifier.height(LifeMashSpacing.micro))
            Text(
                text = comment.createdAt.toRelativeTime(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private fun formatDateLine(startAt: Instant): String {
    val tz = TimeZone.currentSystemDefault()
    val local = startAt.toLocalDateTime(tz)
    val dow = local.dayOfWeek.toKorean()
    return "${local.year}년 ${local.month.number}월 ${local.day}일 ($dow)"
}

private fun formatTimeRange(startAt: Instant, endAt: Instant?): String {
    val tz = TimeZone.currentSystemDefault()
    val s = startAt.toLocalDateTime(tz)
    val startTime = "${s.hour.toString().padStart(2, '0')}:${s.minute.toString().padStart(2, '0')}"
    if (endAt == null) return startTime
    val e = endAt.toLocalDateTime(tz)
    val endTime = "${e.hour.toString().padStart(2, '0')}:${e.minute.toString().padStart(2, '0')}"
    return "$startTime — $endTime"
}

private fun DayOfWeek.toKorean(): String = when (this) {
    DayOfWeek.MONDAY -> "월"
    DayOfWeek.TUESDAY -> "화"
    DayOfWeek.WEDNESDAY -> "수"
    DayOfWeek.THURSDAY -> "목"
    DayOfWeek.FRIDAY -> "금"
    DayOfWeek.SATURDAY -> "토"
    DayOfWeek.SUNDAY -> "일"
}

private fun Instant.toRelativeTime(): String {
    val diff = Clock.System.now() - this
    return when {
        diff.inWholeMinutes < 1 -> "방금 전"
        diff.inWholeHours < 1 -> "${diff.inWholeMinutes}분 전"
        diff.inWholeDays < 1 -> "${diff.inWholeHours}시간 전"
        diff.inWholeDays < 7 -> "${diff.inWholeDays}일 전"
        else -> {
            val local = toLocalDateTime(TimeZone.currentSystemDefault())
            "${local.year}년 ${local.month.number}월 ${local.day}일"
        }
    }
}

private fun splitLocation(location: String): Pair<String, String?> {
    val sepIdx = location.indexOf(',').takeIf { it >= 0 }
        ?: location.indexOf('\n').takeIf { it >= 0 }
        ?: return location to null
    return location.substring(0, sepIdx).trim() to location.substring(sepIdx + 1).trim()
}
