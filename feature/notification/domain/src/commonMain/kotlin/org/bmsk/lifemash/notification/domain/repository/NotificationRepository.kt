package org.bmsk.lifemash.notification.domain.repository

import org.bmsk.lifemash.notification.domain.model.Notification

interface NotificationRepository {
    suspend fun getNotifications(): List<Notification>
    suspend fun getUnreadCount(): Int
    suspend fun markAsRead(notificationId: String)
}
