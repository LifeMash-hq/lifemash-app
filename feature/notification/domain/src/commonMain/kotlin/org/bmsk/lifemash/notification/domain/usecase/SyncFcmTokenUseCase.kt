package org.bmsk.lifemash.notification.domain.usecase

import org.bmsk.lifemash.notification.domain.repository.NotificationKeywordRepository

class SyncFcmTokenUseCase(private val repository: NotificationKeywordRepository) {
    suspend operator fun invoke(fcmToken: String) = repository.syncToFirestore(fcmToken)
}
