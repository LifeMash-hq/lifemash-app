package org.bmsk.lifemash.notification.domain.usecase

import org.bmsk.lifemash.notification.domain.repository.KeywordRepository

class RemoveKeywordUseCase(private val repository: KeywordRepository) {
    suspend operator fun invoke(id: Long) = repository.removeKeyword(id)
}
