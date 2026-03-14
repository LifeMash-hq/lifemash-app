package org.bmsk.lifemash.domain.history.repository

import kotlinx.coroutines.flow.Flow
import org.bmsk.lifemash.domain.core.model.ArticleId

interface ReadingHistoryRepository {
    fun getReadArticleIds(): Flow<Set<ArticleId>>
    suspend fun addToHistory(articleId: ArticleId)
}
