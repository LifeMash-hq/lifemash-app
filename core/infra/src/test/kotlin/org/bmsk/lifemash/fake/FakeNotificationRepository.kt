package org.bmsk.lifemash.fake

import org.bmsk.lifemash.model.notification.NotificationDto
import org.bmsk.lifemash.notification.NotificationRepository
import kotlin.uuid.Uuid

class FakeNotificationRepository : NotificationRepository {
    private val notifications = mutableListOf<NotificationDto>()
    private var counter = 0

    override fun create(userId: Uuid, type: String, actorId: Uuid?, targetId: Uuid?, content: String?): NotificationDto {
        val dto = NotificationDto(
            id = Uuid.random().toString(),
            userId = userId.toString(),
            type = type,
            actorId = actorId?.toString(),
            targetId = targetId?.toString(),
            content = content,
            isRead = false,
            createdAt = "2026-01-01T00:00:${counter++.toString().padStart(2, '0')}Z",
        )
        notifications.add(0, dto) // latest first
        return dto
    }

    override fun findByUser(userId: Uuid, limit: Int): List<NotificationDto> {
        return notifications.filter { it.userId == userId.toString() }.take(limit)
    }

    override fun markAsRead(notificationId: Uuid) {
        val idx = notifications.indexOfFirst { it.id == notificationId.toString() }
        if (idx >= 0) notifications[idx] = notifications[idx].copy(isRead = true)
    }

    override fun getUnreadCount(userId: Uuid): Int {
        return notifications.count { it.userId == userId.toString() && !it.isRead }
    }

    override fun deleteOldest(userId: Uuid, keepCount: Int) {
        val userNotifications = notifications.filter { it.userId == userId.toString() }
        if (userNotifications.size > keepCount) {
            val toRemove = userNotifications.drop(keepCount)
            notifications.removeAll(toRemove)
        }
    }
}
