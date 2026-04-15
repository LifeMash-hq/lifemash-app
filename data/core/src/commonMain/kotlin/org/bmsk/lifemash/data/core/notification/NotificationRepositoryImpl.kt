package org.bmsk.lifemash.data.core.notification

import org.bmsk.lifemash.domain.notification.Notification
import org.bmsk.lifemash.domain.notification.NotificationRepository
import org.bmsk.lifemash.data.remote.notification.NotificationApi
import org.bmsk.lifemash.data.core.notification.toDomainModel

internal class NotificationRepositoryImpl(
    private val api: NotificationApi,
) : NotificationRepository {

    override suspend fun getNotifications(): List<Notification> = api.getNotifications().map { it.toDomainModel() }

    override suspend fun getUnreadCount(): Int = api.getUnreadCount()

    override suspend fun markAsRead(notificationId: String) {
        api.markAsRead(notificationId)
    }
}
