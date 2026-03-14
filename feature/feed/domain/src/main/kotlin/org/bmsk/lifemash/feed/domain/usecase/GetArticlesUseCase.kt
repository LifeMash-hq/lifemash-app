package org.bmsk.lifemash.feed.domain.usecase

import org.bmsk.lifemash.model.Article
import org.bmsk.lifemash.model.ArticleCategory
import org.bmsk.lifemash.feed.domain.repository.ArticleRepository
import javax.inject.Inject

interface GetArticlesUseCase {
    suspend operator fun invoke(category: ArticleCategory): List<Article>
}

internal class GetArticlesUseCaseImpl @Inject constructor(
    private val articleRepository: ArticleRepository
) : GetArticlesUseCase {
    override suspend operator fun invoke(category: ArticleCategory): List<Article> {
        return articleRepository.getArticles(category)
    }
}
