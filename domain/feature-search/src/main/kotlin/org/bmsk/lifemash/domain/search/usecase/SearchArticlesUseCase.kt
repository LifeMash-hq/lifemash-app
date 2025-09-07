package org.bmsk.lifemash.domain.search.usecase

import org.bmsk.lifemash.domain.core.model.Article

interface SearchArticlesUseCase {
    suspend operator fun invoke(query: String): List<Article>
}