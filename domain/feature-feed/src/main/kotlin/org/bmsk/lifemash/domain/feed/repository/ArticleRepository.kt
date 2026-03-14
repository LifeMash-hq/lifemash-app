package org.bmsk.lifemash.domain.feed.repository

import org.bmsk.lifemash.domain.core.model.Article
import org.bmsk.lifemash.domain.core.model.ArticleCategory

interface ArticleRepository {
    suspend fun getArticles(category: ArticleCategory): List<Article>
    suspend fun searchArticles(
        query: String,
        category: String? = null,
        limit: Int = 20,
    ): List<Article>
}
