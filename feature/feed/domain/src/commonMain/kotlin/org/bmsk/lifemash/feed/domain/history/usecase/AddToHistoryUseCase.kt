package org.bmsk.lifemash.feed.domain.history.usecase

import org.bmsk.lifemash.feed.domain.history.repository.ReadingHistoryRepository
import org.bmsk.lifemash.model.Article

class AddToHistoryUseCase(
    private val readingHistoryRepository: ReadingHistoryRepository
) {
    suspend operator fun invoke(article: Article) {
        readingHistoryRepository.addToHistory(article)
    }
}
