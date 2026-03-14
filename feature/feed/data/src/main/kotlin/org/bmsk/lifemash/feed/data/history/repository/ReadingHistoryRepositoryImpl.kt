package org.bmsk.lifemash.feed.data.history.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.bmsk.lifemash.feed.data.history.dao.ReadingHistoryDao
import org.bmsk.lifemash.feed.data.history.entity.ReadingRecordEntity
import org.bmsk.lifemash.model.ArticleId
import org.bmsk.lifemash.feed.domain.history.repository.ReadingHistoryRepository
import java.time.Instant
import javax.inject.Inject

internal class ReadingHistoryRepositoryImpl @Inject constructor(
    private val readingHistoryDao: ReadingHistoryDao
) : ReadingHistoryRepository {

    override fun getReadArticleIds(): Flow<Set<ArticleId>> {
        return readingHistoryDao.getAllReadArticleIds().map { ids ->
            ids.map { ArticleId.from(it) }.toSet()
        }
    }

    override suspend fun addToHistory(articleId: ArticleId) {
        withContext(Dispatchers.IO) {
            readingHistoryDao.insertRecord(
                ReadingRecordEntity(
                    articleId = articleId.value,
                    readAt = Instant.now(),
                )
            )
        }
    }
}
