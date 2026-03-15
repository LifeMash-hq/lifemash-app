package org.bmsk.lifemash.feed.domain.history.repository

import kotlinx.coroutines.flow.Flow
import org.bmsk.lifemash.model.Article
import org.bmsk.lifemash.model.ArticleId

interface ReadingHistoryRepository {
    fun getReadArticleIds(): Flow<Set<ArticleId>>
    suspend fun addToHistory(article: Article)
    fun getReadingHistory(): Flow<List<Article>>
}
