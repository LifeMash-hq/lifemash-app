package org.bmsk.lifemash.notification.impl

import org.bmsk.lifemash.domain.notification.Notification
import org.bmsk.lifemash.domain.notification.NotificationType

/**
 * 도메인 [Notification] → 표현 모델 [NotificationUi] 변환.
 *
 * 도메인의 nullable 필드 조합이 [NotificationType]별로 다르므로, 변환에 실패하는 조합은
 * 안전하게 [NotificationUi.Generic]으로 폴백한다.
 */
internal fun Notification.toUi(): NotificationUi = when (type) {
    NotificationType.COMMENT -> {
        val actor = actorNickname
        val quote = content
        if (actor != null && quote != null) {
            NotificationUi.Comment(
                id = id,
                isUnread = !isRead,
                createdAt = createdAt,
                targetId = targetId,
                actorName = actor,
                quote = quote,
            )
        } else {
            toGeneric()
        }
    }

    NotificationType.FOLLOW -> {
        val actor = actorNickname
        if (actor != null) {
            NotificationUi.Follow(
                id = id,
                isUnread = !isRead,
                createdAt = createdAt,
                targetId = targetId,
                actorName = actor,
            )
        } else {
            toGeneric()
        }
    }

    NotificationType.LIKE -> {
        val actor = actorNickname
        if (actor != null) {
            NotificationUi.Like(
                id = id,
                isUnread = !isRead,
                createdAt = createdAt,
                targetId = targetId,
                actorName = actor,
            )
        } else {
            toGeneric()
        }
    }

    NotificationType.PHOTO -> {
        val actor = actorNickname
        val caption = content
        if (actor != null && caption != null) {
            NotificationUi.Photo(
                id = id,
                isUnread = !isRead,
                createdAt = createdAt,
                targetId = targetId,
                actorName = actor,
                caption = caption,
            )
        } else {
            toGeneric()
        }
    }

    NotificationType.EVENT_REMINDER -> {
        val title = content
        if (title != null) {
            NotificationUi.EventReminder(
                id = id,
                isUnread = !isRead,
                createdAt = createdAt,
                targetId = targetId,
                eventTitle = title,
            )
        } else {
            toGeneric()
        }
    }

    NotificationType.UNKNOWN -> toGeneric()
}

private fun Notification.toGeneric(): NotificationUi.Generic = NotificationUi.Generic(
    id = id,
    isUnread = !isRead,
    createdAt = createdAt,
    targetId = targetId,
    actorName = actorNickname,
    text = content,
)
