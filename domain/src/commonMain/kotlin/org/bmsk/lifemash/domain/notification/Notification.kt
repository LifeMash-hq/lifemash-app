package org.bmsk.lifemash.domain.notification

import kotlin.time.Instant

enum class NotificationType { FOLLOW, LIKE, COMMENT, PHOTO, EVENT_REMINDER, UNKNOWN }

data class Notification(
    val id: String,
    val type: NotificationType,
    val actorNickname: String?,
    val actorProfileImage: String?,
    val targetId: String?,
    val content: String?,
    val isRead: Boolean,
    val createdAt: Instant,
)
