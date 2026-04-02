package org.bmsk.lifemash.notification.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kotlinx.collections.immutable.PersistentList
import kotlin.time.Clock
import kotlin.time.Instant
import org.bmsk.lifemash.designsystem.component.LifeMashButton
import org.bmsk.lifemash.designsystem.component.LifeMashEmptyState
import org.bmsk.lifemash.designsystem.component.LifeMashNotificationItem
import org.bmsk.lifemash.designsystem.component.LifeMashSkeleton
import org.bmsk.lifemash.designsystem.component.LifeMashTopBar
import org.bmsk.lifemash.designsystem.component.NotificationBodyText
import org.bmsk.lifemash.designsystem.theme.LifeMashSpacing
import org.bmsk.lifemash.notification.domain.model.Notification
import org.bmsk.lifemash.notification.domain.model.NotificationType

@Composable
internal fun NotificationScreen(
    uiState: NotificationUiState,
    onRetry: () -> Unit,
    onNotificationClick: (String) -> Unit = {},
) {
    Column(modifier = Modifier.fillMaxSize()) {
        LifeMashTopBar(title = "알림")

        when (uiState) {
            is NotificationUiState.Loading -> LoadingContent()
            is NotificationUiState.Empty -> EmptyContent()
            is NotificationUiState.Error -> ErrorContent(message = uiState.message, onRetry = onRetry)
            is NotificationUiState.Loaded -> NotificationList(
                notifications = uiState.notifications,
                onNotificationClick = onNotificationClick,
            )
        }
    }
}

@Composable
private fun LoadingContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = LifeMashSpacing.xl, vertical = LifeMashSpacing.lg),
    ) {
        repeat(5) {
            LifeMashSkeleton(height = LifeMashSpacing.huge)
            Spacer(modifier = Modifier.height(LifeMashSpacing.md))
        }
    }
}

@Composable
private fun EmptyContent() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        LifeMashEmptyState(
            icon = Icons.Outlined.Notifications,
            title = "새로운 알림이 없어요",
            description = "친구가 일정을 공유하거나\n댓글을 달면 여기에 알려드려요",
        )
    }
}

@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        LifeMashEmptyState(
            icon = Icons.Outlined.Notifications,
            title = message,
            action = {
                LifeMashButton(text = "다시 시도", onClick = onRetry)
            },
        )
    }
}

@Composable
private fun NotificationList(
    notifications: PersistentList<Notification>,
    onNotificationClick: (String) -> Unit = {},
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(notifications, key = { it.id }) { notification ->
            val (emoji, emojiBackground, actorName, actionText, quote) = notificationContent(notification)
            LifeMashNotificationItem(
                emoji = emoji,
                emojiBackground = emojiBackground,
                bodyText = {
                    NotificationBodyText(
                        actorName = actorName,
                        actionText = actionText,
                        quote = quote,
                    )
                },
                timeText = formatRelativeTime(notification.createdAt),
                isUnread = !notification.isRead,
                modifier = Modifier.clickable {
                    notification.targetId?.let { onNotificationClick(it) }
                },
            )
        }
    }
}

private data class NotificationContent(
    val emoji: String,
    val emojiBackground: Color,
    val actorName: String?,
    val actionText: String,
    val quote: String?,
)

private fun notificationContent(notification: Notification): NotificationContent =
    when (notification.type) {
        NotificationType.COMMENT -> NotificationContent(
            emoji = "\uD83D\uDCAC",
            emojiBackground = Color(0x1F6C5CE7),
            actorName = notification.actorNickname,
            actionText = "님이 댓글 달았습니다",
            quote = notification.content,
        )
        NotificationType.FOLLOW -> NotificationContent(
            emoji = "\uD83D\uDC64",
            emojiBackground = Color(0x1F6C5CE7),
            actorName = notification.actorNickname,
            actionText = "님이 회원님을 팔로우합니다",
            quote = null,
        )
        NotificationType.LIKE -> NotificationContent(
            emoji = "\u2764\uFE0F",
            emojiBackground = Color(0x1FEF4444),
            actorName = notification.actorNickname,
            actionText = "님이 좋아요를 눌렀습니다",
            quote = null,
        )
        NotificationType.PHOTO -> NotificationContent(
            emoji = "\uD83D\uDCF7",
            emojiBackground = Color(0x1F10B981),
            actorName = notification.actorNickname,
            actionText = "님이 새 사진을 올렸습니다",
            quote = notification.content,
        )
        NotificationType.EVENT_REMINDER -> NotificationContent(
            emoji = "\uD83D\uDCC5",
            emojiBackground = Color(0x1FE17051),
            actorName = null,
            actionText = "오늘 ${notification.content ?: ""} 일정",
            quote = null,
        )
        NotificationType.UNKNOWN -> NotificationContent(
            emoji = "\uD83D\uDD14",
            emojiBackground = Color(0x1F888888),
            actorName = notification.actorNickname,
            actionText = notification.content ?: "새 알림",
            quote = null,
        )
    }

private fun formatRelativeTime(instant: Instant): String {
    val now = Clock.System.now()
    val diff = now - instant
    return when {
        diff.inWholeDays >= 365 -> "${diff.inWholeDays / 365}년 전"
        diff.inWholeDays >= 30 -> "${diff.inWholeDays / 30}개월 전"
        diff.inWholeDays >= 1 -> "${diff.inWholeDays}일 전"
        diff.inWholeHours >= 1 -> "${diff.inWholeHours}시간 전"
        diff.inWholeMinutes >= 1 -> "${diff.inWholeMinutes}분 전"
        else -> "방금 전"
    }
}
