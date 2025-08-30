package org.bmsk.lifemash.domain.feed.repository

import org.bmsk.lifemash.domain.core.model.Article
import org.bmsk.lifemash.domain.core.model.ArticleCategory

interface ArticleRepository {
    suspend fun getArticles(category: ArticleCategory): List<Article>
}
