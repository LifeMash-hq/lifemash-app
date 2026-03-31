package org.bmsk.lifemash.notification.data.repository

import org.bmsk.lifemash.notification.data.api.NotificationApi
import org.bmsk.lifemash.notification.data.mapper.toDomain
import org.bmsk.lifemash.notification.domain.model.Notification
import org.bmsk.lifemash.notification.domain.repository.NotificationRepository

internal class NotificationRepositoryImpl(
    private val api: NotificationApi,
) : NotificationRepository {

    override suspend fun getNotifications(): List<Notification> =
        api.getNotifications().map { it.toDomain() }

    override suspend fun getUnreadCount(): Int = api.getUnreadCount()

    override suspend fun markAsRead(notificationId: String) = api.markAsRead(notificationId)
}
