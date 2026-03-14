package org.bmsk.lifemash.domain.history.usecase

import org.bmsk.lifemash.domain.core.model.ArticleId
import org.bmsk.lifemash.domain.history.repository.ReadingHistoryRepository
import javax.inject.Inject

class AddToHistoryUseCase @Inject constructor(
    private val readingHistoryRepository: ReadingHistoryRepository
) {
    suspend operator fun invoke(articleId: ArticleId) {
        readingHistoryRepository.addToHistory(articleId)
    }
}
