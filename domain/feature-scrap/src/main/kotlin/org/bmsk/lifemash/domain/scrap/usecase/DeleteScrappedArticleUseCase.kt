package org.bmsk.lifemash.domain.scrap.usecase

import org.bmsk.lifemash.domain.core.model.ArticleId
import org.bmsk.lifemash.domain.scrap.repository.ScrapRepository
import javax.inject.Inject

interface DeleteScrappedArticleUseCase {
    suspend operator fun invoke(articleId: ArticleId)
}

internal class DeleteScrappedArticleUseCaseImpl @Inject constructor(
    private val scrapRepository: ScrapRepository
) : DeleteScrappedArticleUseCase {
    override suspend operator fun invoke(articleId: ArticleId) {
        scrapRepository.deleteScrappedArticle(articleId)
    }
}
