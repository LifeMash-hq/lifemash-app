package org.bmsk.lifemash.domain.notification

interface NotificationRepository {
    suspend fun getNotifications(): List<Notification>
    suspend fun getUnreadCount(): Int
    suspend fun markAsRead(notificationId: String)
}
