package org.bmsk.lifemash.scrap.domain.usecase

import org.bmsk.lifemash.model.Article
import org.bmsk.lifemash.scrap.domain.repository.ScrapRepository

interface AddScrapUseCase {
    suspend operator fun invoke(article: Article)
}

class AddScrapUseCaseImpl(
    private val scrapRepository: ScrapRepository
) : AddScrapUseCase {
    override suspend operator fun invoke(article: Article) {
        scrapRepository.addScrappedArticle(article)
    }
}
