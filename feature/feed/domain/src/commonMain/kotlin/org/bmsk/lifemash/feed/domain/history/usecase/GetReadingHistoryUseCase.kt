package org.bmsk.lifemash.feed.domain.history.usecase

import kotlinx.coroutines.flow.Flow
import org.bmsk.lifemash.feed.domain.history.repository.ReadingHistoryRepository
import org.bmsk.lifemash.model.Article

interface GetReadingHistoryUseCase {
    operator fun invoke(): Flow<List<Article>>
}

class GetReadingHistoryUseCaseImpl(
    private val readingHistoryRepository: ReadingHistoryRepository
) : GetReadingHistoryUseCase {
    override fun invoke(): Flow<List<Article>> {
        return readingHistoryRepository.getReadingHistory()
    }
}
