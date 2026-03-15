package org.bmsk.lifemash.feed.data.history.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import org.bmsk.lifemash.feed.data.history.dao.ReadingHistoryDao
import org.bmsk.lifemash.feed.data.history.entity.toDomain
import org.bmsk.lifemash.feed.data.history.entity.toReadingRecordEntity
import org.bmsk.lifemash.feed.domain.history.repository.ReadingHistoryRepository
import org.bmsk.lifemash.model.Article
import org.bmsk.lifemash.model.ArticleId

class ReadingHistoryRepositoryImpl(
    private val readingHistoryDao: ReadingHistoryDao
) : ReadingHistoryRepository {

    override fun getReadArticleIds(): Flow<Set<ArticleId>> {
        return readingHistoryDao.getAllReadArticleIds().map { ids ->
            ids.map { ArticleId.from(it) }.toSet()
        }
    }

    override suspend fun addToHistory(article: Article) {
        withContext(Dispatchers.IO) {
            readingHistoryDao.insertRecord(
                article.toReadingRecordEntity(readAt = Clock.System.now())
            )
        }
    }

    override fun getReadingHistory(): Flow<List<Article>> {
        return readingHistoryDao.getAllReadingHistory().map { records ->
            records.map { it.toDomain() }
        }
    }
}
