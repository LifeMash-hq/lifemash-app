package org.bmsk.lifemash.notification.domain.usecase

import org.bmsk.lifemash.notification.domain.repository.NotificationKeywordRepository

class RemoveKeywordUseCase(private val repository: NotificationKeywordRepository) {
    suspend operator fun invoke(id: Long) = repository.removeKeyword(id)
}
