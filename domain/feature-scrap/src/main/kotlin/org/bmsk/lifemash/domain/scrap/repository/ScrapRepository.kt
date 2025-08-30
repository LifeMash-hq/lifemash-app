package org.bmsk.lifemash.domain.scrap.repository

import org.bmsk.lifemash.domain.core.model.Article
import org.bmsk.lifemash.domain.core.model.ArticleId

interface ScrapRepository {
    suspend fun getScrappedArticles(): List<Article>
    suspend fun addScrappedArticle(article: Article)
    suspend fun deleteScrappedArticle(articleId: ArticleId)
}
