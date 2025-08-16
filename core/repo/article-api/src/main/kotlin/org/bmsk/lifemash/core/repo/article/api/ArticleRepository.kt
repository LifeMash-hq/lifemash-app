package org.bmsk.lifemash.core.repo.article.api

interface ArticleRepository {
    suspend fun getArticles(category: ArticleCategory): List<Article>
}