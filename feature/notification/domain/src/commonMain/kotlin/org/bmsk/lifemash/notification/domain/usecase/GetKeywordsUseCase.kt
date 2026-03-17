package org.bmsk.lifemash.notification.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.bmsk.lifemash.notification.domain.model.NotificationKeyword
import org.bmsk.lifemash.notification.domain.repository.KeywordRepository

class GetKeywordsUseCase(private val repository: KeywordRepository) {
    operator fun invoke(): Flow<List<NotificationKeyword>> = repository.getKeywords()
}
