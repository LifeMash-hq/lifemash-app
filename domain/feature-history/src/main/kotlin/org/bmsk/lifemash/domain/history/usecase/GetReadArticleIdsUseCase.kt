package org.bmsk.lifemash.domain.history.usecase

import kotlinx.coroutines.flow.Flow
import org.bmsk.lifemash.model.ArticleId
import org.bmsk.lifemash.domain.history.repository.ReadingHistoryRepository
import javax.inject.Inject

class GetReadArticleIdsUseCase @Inject constructor(
    private val readingHistoryRepository: ReadingHistoryRepository
) {
    operator fun invoke(): Flow<Set<ArticleId>> {
        return readingHistoryRepository.getReadArticleIds()
    }
}
