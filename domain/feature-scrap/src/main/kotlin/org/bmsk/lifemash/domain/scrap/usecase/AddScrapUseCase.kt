package org.bmsk.lifemash.domain.scrap.usecase

import org.bmsk.lifemash.domain.core.model.Article
import org.bmsk.lifemash.domain.scrap.repository.ScrapRepository
import javax.inject.Inject

class AddScrapUseCase @Inject constructor(
    private val scrapRepository: ScrapRepository
) {
    suspend operator fun invoke(article: Article) {
        scrapRepository.addScrappedArticle(article)
    }
}
