package org.bmsk.lifemash.domain.usecase.notification

import org.bmsk.lifemash.domain.notification.NotificationRepository

class MarkNotificationReadUseCase(private val repository: NotificationRepository) {
    suspend operator fun invoke(notificationId: String) = repository.markAsRead(notificationId)
}
