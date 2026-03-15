package org.bmsk.lifemash.feed.domain.usecase

import org.bmsk.lifemash.model.Article
import org.bmsk.lifemash.feed.domain.repository.ArticleRepository

interface SearchArticlesUseCase {
    suspend operator fun invoke(
        query: String,
        category: String? = null,
        limit: Int = 20,
    ): List<Article>
}

class SearchArticlesUseCaseImpl(
    private val articleRepository: ArticleRepository
) : SearchArticlesUseCase {
    override suspend operator fun invoke(
        query: String,
        category: String?,
        limit: Int,
    ): List<Article> {
        return articleRepository.searchArticles(
            query = query,
            category = category,
            limit = limit,
        )
    }
}
