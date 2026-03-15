package org.bmsk.lifemash.notification.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.bmsk.lifemash.notification.domain.model.NotificationKeyword
import org.bmsk.lifemash.notification.domain.repository.NotificationKeywordRepository

class GetKeywordsUseCase(private val repository: NotificationKeywordRepository) {
    operator fun invoke(): Flow<List<NotificationKeyword>> = repository.getKeywords()
}
