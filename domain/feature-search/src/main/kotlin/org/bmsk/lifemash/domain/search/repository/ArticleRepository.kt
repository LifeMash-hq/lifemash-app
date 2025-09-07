package org.bmsk.lifemash.domain.search.repository

import org.bmsk.lifemash.domain.core.model.Article

interface ArticleRepository {
    fun search(query: String): List<Article>
}