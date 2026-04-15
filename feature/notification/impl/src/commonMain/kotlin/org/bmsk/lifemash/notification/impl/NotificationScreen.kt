@file:OptIn(kotlin.time.ExperimentalTime::class)

package org.bmsk.lifemash.notification.impl

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.collections.immutable.PersistentList
import org.bmsk.lifemash.designsystem.component.LifeMashButton
import org.bmsk.lifemash.designsystem.component.LifeMashEmptyState
import org.bmsk.lifemash.designsystem.component.LifeMashNotificationItem
import org.bmsk.lifemash.designsystem.component.LifeMashSkeleton
import org.bmsk.lifemash.designsystem.component.LifeMashTopBar
import org.bmsk.lifemash.designsystem.component.NotificationBodyText
import org.bmsk.lifemash.designsystem.theme.LifeMashSpacing
import lifemash.feature.notification.impl.generated.resources.Res
import lifemash.feature.notification.impl.generated.resources.notification_empty_description
import lifemash.feature.notification.impl.generated.resources.notification_empty_title
import lifemash.feature.notification.impl.generated.resources.notification_retry
import lifemash.feature.notification.impl.generated.resources.notification_title
import lifemash.feature.notification.impl.generated.resources.time_days_ago
import lifemash.feature.notification.impl.generated.resources.time_hours_ago
import lifemash.feature.notification.impl.generated.resources.time_just_now
import lifemash.feature.notification.impl.generated.resources.time_minutes_ago
import lifemash.feature.notification.impl.generated.resources.time_months_ago
import lifemash.feature.notification.impl.generated.resources.time_years_ago
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Clock
import kotlin.time.Instant

@Composable
internal fun NotificationScreen(
    uiState: NotificationUiState,
    onRetry: () -> Unit,
    onNotificationClick: (String) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
        LifeMashTopBar(title = stringResource(Res.string.notification_title))

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
            title = stringResource(Res.string.notification_empty_title),
            description = stringResource(Res.string.notification_empty_description),
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
                LifeMashButton(text = stringResource(Res.string.notification_retry), onClick = onRetry)
            },
        )
    }
}

@Composable
private fun NotificationList(
    notifications: PersistentList<NotificationUi>,
    onNotificationClick: (String) -> Unit,
) {
    // 리스트 단위로 한 번만 now 고정 → 매 항목·매 리컴포지션 Clock.now() 호출 방지.
    val now = remember { Clock.System.now() }
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(notifications, key = { it.id }) { ui ->
            NotificationRow(
                ui = ui,
                now = now,
                onNotificationClick = onNotificationClick,
            )
        }
    }
}

@Composable
private fun NotificationRow(
    ui: NotificationUi,
    now: Instant,
    onNotificationClick: (String) -> Unit,
) {
    val style = notificationVisualStyle(ui.visual)
    val targetId = ui.targetId
    LifeMashNotificationItem(
        emoji = style.emoji,
        emojiBackground = style.background,
        bodyText = { NotificationBody(ui = ui) },
        timeText = relativeTimeText(instant = ui.createdAt, now = now),
        isUnread = ui.isUnread,
        modifier = if (targetId != null) {
            Modifier.clickable { onNotificationClick(targetId) }
        } else {
            Modifier
        },
    )
}

@Composable
private fun NotificationBody(ui: NotificationUi) {
    NotificationBodyText(
        actorName = ui.actorName,
        actionText = resolveActionText(ui.actionText),
        quote = ui.quote,
    )
}

@Composable
private fun resolveActionText(actionText: NotificationUi.ActionText): String = when (actionText) {
    is NotificationUi.ActionText.Resource ->
        stringResource(actionText.res, *actionText.formatArgs.toTypedArray())
    is NotificationUi.ActionText.Literal -> actionText.text
}

@Composable
private fun relativeTimeText(instant: Instant, now: Instant): String {
    val diff = now - instant
    return when {
        diff.inWholeDays >= 365 ->
            stringResource(Res.string.time_years_ago, (diff.inWholeDays / 365).toInt())
        diff.inWholeDays >= 30 ->
            stringResource(Res.string.time_months_ago, (diff.inWholeDays / 30).toInt())
        diff.inWholeDays >= 1 ->
            stringResource(Res.string.time_days_ago, diff.inWholeDays.toInt())
        diff.inWholeHours >= 1 ->
            stringResource(Res.string.time_hours_ago, diff.inWholeHours.toInt())
        diff.inWholeMinutes >= 1 ->
            stringResource(Res.string.time_minutes_ago, diff.inWholeMinutes.toInt())
        else -> stringResource(Res.string.time_just_now)
    }
}
