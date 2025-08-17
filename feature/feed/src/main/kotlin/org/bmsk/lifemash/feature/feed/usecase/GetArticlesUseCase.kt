package org.bmsk.lifemash.feature.feed.usecase

import org.bmsk.lifemash.core.repo.article.api.Article
import org.bmsk.lifemash.core.repo.article.api.ArticleCategory
import org.bmsk.lifemash.core.repo.article.api.ArticleRepository
import javax.inject.Inject

internal interface GetArticlesUseCase {
    suspend operator fun invoke(category: ArticleCategory): List<Article>
}

internal class GetArticlesUseCaseImpl @Inject constructor(
    private val articleRepository: ArticleRepository,
) : GetArticlesUseCase {
    override suspend fun invoke(category: ArticleCategory): List<Article> {
        return articleRepository.getArticles(category)
    }
}