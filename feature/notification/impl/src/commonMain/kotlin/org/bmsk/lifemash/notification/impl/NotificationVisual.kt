package org.bmsk.lifemash.notification.impl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * 알림 항목의 시각 카테고리. 이모지/배경색 결정의 단일 출처.
 *
 * 도메인 [org.bmsk.lifemash.domain.notification.NotificationType]과 1:1로 매핑될 수 있지만
 * UI 표현 분류가 도메인 분류와 항상 일치한다는 보장이 없으므로 분리해서 둔다.
 */
internal enum class NotificationVisual {
    COMMENT,
    FOLLOW,
    LIKE,
    PHOTO,
    EVENT,
    GENERIC,
}

@Immutable
internal data class NotificationVisualStyle(
    val emoji: String,
    val background: Color,
)

@Composable
internal fun notificationVisualStyle(visual: NotificationVisual): NotificationVisualStyle =
    when (visual) {
        NotificationVisual.COMMENT -> NotificationVisualStyle(
            emoji = "\uD83D\uDCAC",
            background = Color(0x1F6C5CE7),
        )
        NotificationVisual.FOLLOW -> NotificationVisualStyle(
            emoji = "\uD83D\uDC64",
            background = Color(0x1F6C5CE7),
        )
        NotificationVisual.LIKE -> NotificationVisualStyle(
            emoji = "\u2764\uFE0F",
            background = Color(0x1FEF4444),
        )
        NotificationVisual.PHOTO -> NotificationVisualStyle(
            emoji = "\uD83D\uDCF7",
            background = Color(0x1F10B981),
        )
        NotificationVisual.EVENT -> NotificationVisualStyle(
            emoji = "\uD83D\uDCC5",
            background = Color(0x1FE17051),
        )
        NotificationVisual.GENERIC -> NotificationVisualStyle(
            emoji = "\uD83D\uDD14",
            background = Color(0x1F888888),
        )
    }
