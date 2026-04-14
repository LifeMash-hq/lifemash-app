package org.bmsk.lifemash.data.core.notification

import org.bmsk.lifemash.domain.notification.Notification
import org.bmsk.lifemash.domain.notification.NotificationType
import org.bmsk.lifemash.data.remote.notification.dto.NotificationResponse

internal fun NotificationResponse.toDomainModel(): Notification =
    Notification(
        id = id,
        type = when (type) {
            "follow" -> NotificationType.FOLLOW
            "like" -> NotificationType.LIKE
            "comment" -> NotificationType.COMMENT
            "photo" -> NotificationType.PHOTO
            "event_reminder" -> NotificationType.EVENT_REMINDER
            else -> NotificationType.UNKNOWN
        },
        actorNickname = actorNickname,
        actorProfileImage = actorProfileImage,
        targetId = targetId,
        content = content,
        isRead = isRead,
        createdAt = kotlin.time.Instant.parse(createdAt),
    )
