package org.bmsk.lifemash.domain.scrap.usecase

import org.bmsk.lifemash.domain.core.model.Article
import org.bmsk.lifemash.domain.scrap.repository.ScrapRepository

interface AddScrapUseCase {
    suspend operator fun invoke(article: Article)
}

internal class AddScrapUseCaseImpl(
    private val scrapRepository: ScrapRepository
) : AddScrapUseCase {
    override suspend operator fun invoke(article: Article) {
        scrapRepository.addScrappedArticle(article)
    }
}
