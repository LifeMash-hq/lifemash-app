package org.bmsk.lifemash.domain.scrap.repository

import kotlinx.coroutines.flow.Flow
import org.bmsk.lifemash.domain.core.model.Article
import org.bmsk.lifemash.domain.core.model.ArticleId

interface ScrapRepository {
    fun getScrappedArticles(): Flow<List<Article>>
    suspend fun addScrappedArticle(article: Article)
    suspend fun deleteScrappedArticle(articleId: ArticleId)
}
