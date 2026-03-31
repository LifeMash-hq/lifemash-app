package org.bmsk.lifemash.notification.ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.collections.immutable.persistentListOf
import kotlin.time.Instant
import org.bmsk.lifemash.designsystem.theme.LifeMashTheme
import org.bmsk.lifemash.notification.domain.model.Notification
import org.bmsk.lifemash.notification.domain.model.NotificationType

private val sampleNotifications = persistentListOf(
    Notification(
        id = "1",
        type = NotificationType.COMMENT,
        actorNickname = "이수아",
        actorProfileImage = null,
        targetId = null,
        content = "삼성이었어?!?! 축하해 진짜",
        isRead = false,
        createdAt = Instant.parse("2026-03-31T09:55:00Z"),
    ),
    Notification(
        id = "2",
        type = NotificationType.FOLLOW,
        actorNickname = "정재원",
        actorProfileImage = null,
        targetId = null,
        content = null,
        isRead = true,
        createdAt = Instant.parse("2026-03-31T07:00:00Z"),
    ),
    Notification(
        id = "3",
        type = NotificationType.PHOTO,
        actorNickname = "이수아",
        actorProfileImage = null,
        targetId = null,
        content = "결혼식",
        isRead = true,
        createdAt = Instant.parse("2026-03-30T10:00:00Z"),
    ),
    Notification(
        id = "4",
        type = NotificationType.EVENT_REMINDER,
        actorNickname = null,
        actorProfileImage = null,
        targetId = null,
        content = "청담 오마카세",
        isRead = true,
        createdAt = Instant.parse("2026-03-30T08:00:00Z"),
    ),
)

@Preview(name = "Light - Loaded", showBackground = true)
@Preview(name = "Dark - Loaded", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
internal fun NotificationScreenPreview_Loaded() {
    LifeMashTheme {
        NotificationScreen(
            uiState = NotificationUiState.Loaded(notifications = sampleNotifications),
            onRetry = {},
        )
    }
}

@Preview(name = "Light - Empty", showBackground = true)
@Preview(name = "Dark - Empty", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
internal fun NotificationScreenPreview_Empty() {
    LifeMashTheme {
        NotificationScreen(
            uiState = NotificationUiState.Empty,
            onRetry = {},
        )
    }
}
