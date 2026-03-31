package org.bmsk.lifemash.social

import org.bmsk.lifemash.model.notification.NotificationDto
import kotlin.uuid.Uuid

class NotificationService(
    private val notificationRepository: NotificationRepository,
) {
    fun createNotification(userId: Uuid, type: String, actorId: Uuid?, targetId: Uuid?, content: String? = null) {
        notificationRepository.create(userId, type, actorId, targetId, content)
        notificationRepository.deleteOldest(userId, keepCount = 100)
    }

    fun getNotifications(userId: Uuid): List<NotificationDto> {
        return notificationRepository.findByUser(userId)
    }

    fun markAsRead(notificationId: Uuid) {
        notificationRepository.markAsRead(notificationId)
    }

    fun getUnreadCount(userId: Uuid): Int {
        return notificationRepository.getUnreadCount(userId)
    }
}
