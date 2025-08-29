package org.bmsk.lifemash.domain.scrap.usecase

import org.bmsk.lifemash.domain.core.model.ArticleId
import org.bmsk.lifemash.domain.scrap.repository.ScrapRepository
import javax.inject.Inject

class DeleteScrappedArticleUseCase @Inject constructor(
    private val scrapRepository: ScrapRepository
) {
    suspend operator fun invoke(articleId: ArticleId) {
        scrapRepository.deleteScrappedArticle(articleId)
    }
}
