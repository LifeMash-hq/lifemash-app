package org.bmsk.lifemash.domain.search.repository

import org.bmsk.lifemash.model.Article

interface NewsRepository {
    suspend fun getGoogleNews(query: String): List<Article>
}
