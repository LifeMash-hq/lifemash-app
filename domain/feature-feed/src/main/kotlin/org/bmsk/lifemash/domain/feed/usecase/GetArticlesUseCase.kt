package org.bmsk.lifemash.domain.feed.usecase

import org.bmsk.lifemash.domain.core.model.Article
import org.bmsk.lifemash.domain.core.model.ArticleCategory
import org.bmsk.lifemash.domain.feed.repository.ArticleRepository
import javax.inject.Inject

class GetArticlesUseCase @Inject constructor(
    private val articleRepository: ArticleRepository
) {
    suspend operator fun invoke(category: ArticleCategory): List<Article> {
        return articleRepository.getArticles(category)
    }
}
