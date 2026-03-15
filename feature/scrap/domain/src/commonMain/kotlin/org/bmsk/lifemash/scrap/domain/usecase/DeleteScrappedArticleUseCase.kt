package org.bmsk.lifemash.scrap.domain.usecase

import org.bmsk.lifemash.model.ArticleId
import org.bmsk.lifemash.scrap.domain.repository.ScrapRepository

interface DeleteScrappedArticleUseCase {
    suspend operator fun invoke(articleId: ArticleId)
}

class DeleteScrappedArticleUseCaseImpl(
    private val scrapRepository: ScrapRepository
) : DeleteScrappedArticleUseCase {
    override suspend operator fun invoke(articleId: ArticleId) {
        scrapRepository.deleteScrappedArticle(articleId)
    }
}
