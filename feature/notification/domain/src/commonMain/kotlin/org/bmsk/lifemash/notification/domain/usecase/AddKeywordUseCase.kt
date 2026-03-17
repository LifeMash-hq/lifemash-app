package org.bmsk.lifemash.notification.domain.usecase

import org.bmsk.lifemash.notification.domain.model.Keyword
import org.bmsk.lifemash.notification.domain.repository.KeywordRepository

class AddKeywordUseCase(private val repository: KeywordRepository) {
    suspend operator fun invoke(raw: String) {
        val keyword = Keyword.from(raw)
        repository.addKeyword(keyword)
    }
}
