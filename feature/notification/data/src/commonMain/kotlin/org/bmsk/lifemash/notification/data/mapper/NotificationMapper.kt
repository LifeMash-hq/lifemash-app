package org.bmsk.lifemash.notification.data.mapper

import org.bmsk.lifemash.model.notification.NotificationDto
import org.bmsk.lifemash.notification.domain.model.Notification
import org.bmsk.lifemash.notification.domain.model.NotificationType

internal fun NotificationDto.toDomain(): Notification = Notification(
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
