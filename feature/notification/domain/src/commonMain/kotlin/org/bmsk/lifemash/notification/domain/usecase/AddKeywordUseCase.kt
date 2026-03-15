package org.bmsk.lifemash.notification.domain.usecase

import org.bmsk.lifemash.notification.domain.repository.NotificationKeywordRepository

class AddKeywordUseCase(private val repository: NotificationKeywordRepository) {
    suspend operator fun invoke(keyword: String) = repository.addKeyword(keyword.trim())
}
