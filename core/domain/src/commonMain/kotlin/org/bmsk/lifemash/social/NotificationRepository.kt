package org.bmsk.lifemash.social

import org.bmsk.lifemash.model.notification.NotificationDto
import kotlin.uuid.Uuid

interface NotificationRepository {
    fun create(userId: Uuid, type: String, actorId: Uuid?, targetId: Uuid?, content: String? = null): NotificationDto
    fun findByUser(userId: Uuid, limit: Int = 100): List<NotificationDto>
    fun markAsRead(notificationId: Uuid)
    fun getUnreadCount(userId: Uuid): Int
    fun deleteOldest(userId: Uuid, keepCount: Int = 100)
}
