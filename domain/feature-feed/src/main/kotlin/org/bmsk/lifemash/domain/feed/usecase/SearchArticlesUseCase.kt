package org.bmsk.lifemash.domain.feed.usecase

import org.bmsk.lifemash.domain.core.model.Article
import org.bmsk.lifemash.domain.feed.repository.ArticleRepository
import javax.inject.Inject

interface SearchArticlesUseCase {
    suspend operator fun invoke(
        query: String,
        category: String? = null,
        limit: Int = 20,
    ): List<Article>
}

internal class SearchArticlesUseCaseImpl @Inject constructor(
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
