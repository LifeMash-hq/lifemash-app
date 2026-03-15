package org.bmsk.lifemash.feed.domain.usecase

import org.bmsk.lifemash.model.Article
import org.bmsk.lifemash.model.ArticleCategory
import org.bmsk.lifemash.feed.domain.repository.ArticleRepository

interface GetArticlesUseCase {
    suspend operator fun invoke(category: ArticleCategory): List<Article>
}

class GetArticlesUseCaseImpl(
    private val articleRepository: ArticleRepository
) : GetArticlesUseCase {
    override suspend operator fun invoke(category: ArticleCategory): List<Article> {
        return articleRepository.getArticles(category)
    }
}
