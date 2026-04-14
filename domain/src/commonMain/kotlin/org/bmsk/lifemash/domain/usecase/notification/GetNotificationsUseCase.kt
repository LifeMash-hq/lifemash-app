package org.bmsk.lifemash.domain.usecase.notification

import org.bmsk.lifemash.domain.notification.Notification
import org.bmsk.lifemash.domain.notification.NotificationRepository

class GetNotificationsUseCase(private val repository: NotificationRepository) {
    suspend operator fun invoke(): List<Notification> = repository.getNotifications()
}
