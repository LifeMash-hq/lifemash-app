package org.bmsk.lifemash.eventdetail.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Notes
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.bmsk.lifemash.feature.designsystem.component.AvatarSize
import org.bmsk.lifemash.feature.designsystem.component.LifeMashAvatar
import org.bmsk.lifemash.feature.designsystem.component.LifeMashButton

@Composable
fun EventDetailScreen(
    uiState: EventDetailUiState,
    onBack: () -> Unit = {},
    onJoinToggle: (EventDetailUiState.Loaded) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Box(modifier.fillMaxSize().statusBarsPadding()) {
        when (uiState) {
            is EventDetailUiState.Loading -> Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) { CircularProgressIndicator() }

            is EventDetailUiState.Error -> Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) { Text(uiState.message) }

            is EventDetailUiState.Loaded -> LoadedContent(
                state = uiState,
                onBack = onBack,
                onJoinToggle = onJoinToggle,
            )
        }
    }
}

@Composable
private fun LoadedContent(
    state: EventDetailUiState.Loaded,
    onBack: () -> Unit,
    onJoinToggle: (EventDetailUiState.Loaded) -> Unit,
) {
    Column(Modifier.fillMaxSize()) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp),
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
            IconButton(onClick = {}) {
                Icon(Icons.Outlined.CalendarMonth, contentDescription = "캘린더 추가")
            }
            IconButton(onClick = {}) {
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
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    contentAlignment = Alignment.BottomStart,
                ) {
                    Column {
                        Text(
                            text = state.title,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = Color.White,
                        )
                        state.sharedByNickname?.let { nickname ->
                            Text(
                                text = "$nickname 님이 공유",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.85f),
                            )
                        }
                    }
                }
            }

            // Detail section
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                DetailRow(icon = { Icon(Icons.Outlined.CalendarMonth, contentDescription = null, modifier = Modifier.size(36.dp)) }, text = state.date)
                state.location?.let {
                    Spacer(Modifier.height(8.dp))
                    DetailRow(icon = { Icon(Icons.Outlined.LocationOn, contentDescription = null, modifier = Modifier.size(36.dp)) }, text = it)
                }
                state.description?.let {
                    Spacer(Modifier.height(8.dp))
                    DetailRow(icon = { Icon(Icons.Outlined.Notes, contentDescription = null, modifier = Modifier.size(36.dp)) }, text = it)
                }
            }

            // Attendees section
            if (state.attendees.isNotEmpty()) {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "참석자",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    )
                    Spacer(Modifier.height(8.dp))
                    AttendeesRow(attendees = state.attendees)
                }
                Spacer(Modifier.height(16.dp))
            }

            // Comments section
            if (state.comments.isNotEmpty()) {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "댓글 ${state.comments.size}",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    )
                    Spacer(Modifier.height(8.dp))
                    state.comments.forEach { comment ->
                        CommentItem(comment = comment)
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }

        // Sticky bottom button
        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            LifeMashButton(
                text = if (state.isJoined) "참여 중" else "참여",
                onClick = { onJoinToggle(state) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun DetailRow(icon: @Composable () -> Unit, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        icon()
        Spacer(Modifier.width(12.dp))
        Text(text = text, style = MaterialTheme.typography.bodyMedium)
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
            Spacer(Modifier.width(4.dp))
            Text(
                text = "+$overflow 명",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
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
        Spacer(Modifier.width(8.dp))
        Column {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = comment.authorNickname,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                )
                Text(
                    text = comment.createdAt,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(text = comment.content, style = MaterialTheme.typography.bodySmall)
        }
    }
}
