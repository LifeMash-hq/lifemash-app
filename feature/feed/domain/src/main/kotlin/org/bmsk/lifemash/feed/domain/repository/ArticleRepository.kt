package org.bmsk.lifemash.feed.domain.repository

import org.bmsk.lifemash.model.Article
import org.bmsk.lifemash.model.ArticleCategory

interface ArticleRepository {
    suspend fun getArticles(category: ArticleCategory): List<Article>
    suspend fun searchArticles(
        query: String,
        category: String? = null,
        limit: Int = 20,
    ): List<Article>
}
