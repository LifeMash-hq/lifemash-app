package org.bmsk.lifemash.scrap.domain.usecase

import org.bmsk.lifemash.model.ArticleId
import org.bmsk.lifemash.scrap.domain.repository.ScrapRepository
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
