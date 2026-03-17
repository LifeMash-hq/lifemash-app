package org.bmsk.lifemash.notification.domain.usecase

import org.bmsk.lifemash.notification.domain.repository.KeywordSyncRepository

class SyncKeywordsUseCase(private val repository: KeywordSyncRepository) {
    suspend operator fun invoke(deviceToken: String) = repository.syncKeywords(deviceToken)
}
