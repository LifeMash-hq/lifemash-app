@file:OptIn(kotlin.time.ExperimentalTime::class)

package org.bmsk.lifemash.notification.impl

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import kotlinx.collections.immutable.persistentListOf
import kotlin.time.Instant
import org.bmsk.lifemash.designsystem.component.LifeMashBackground
import org.bmsk.lifemash.designsystem.theme.LifeMashTheme

private val sampleNotifications = persistentListOf(
    NotificationUi.Comment(
        id = "1",
        isUnread = true,
        createdAt = Instant.parse("2026-03-31T09:55:00Z"),
        targetId = null,
        actorName = "이수아",
        quote = "삼성이었어?!?! 축하해 진짜",
    ),
    NotificationUi.Follow(
        id = "2",
        isUnread = false,
        createdAt = Instant.parse("2026-03-31T07:00:00Z"),
        targetId = null,
        actorName = "정재원",
    ),
    NotificationUi.Photo(
        id = "3",
        isUnread = false,
        createdAt = Instant.parse("2026-03-30T10:00:00Z"),
        targetId = null,
        actorName = "이수아",
        caption = "결혼식",
    ),
    NotificationUi.EventReminder(
        id = "4",
        isUnread = false,
        createdAt = Instant.parse("2026-03-30T08:00:00Z"),
        targetId = null,
        eventTitle = "청담 오마카세",
    ),
)

private class NotificationUiStateProvider : PreviewParameterProvider<NotificationUiState> {
    override val values = sequenceOf(
        NotificationUiState.Loading,
        NotificationUiState.Loaded(notifications = sampleNotifications),
        NotificationUiState.Empty,
        NotificationUiState.Error(message = "알림을 불러올 수 없습니다"),
    )
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES)
@Composable
internal fun NotificationScreenPreview(
    @PreviewParameter(NotificationUiStateProvider::class) uiState: NotificationUiState,
) {
    LifeMashTheme {
        LifeMashBackground {
            NotificationScreen(
                uiState = uiState,
                onRetry = {},
                onNotificationClick = {},
            )
        }
    }
}
