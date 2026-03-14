package org.bmsk.lifemash.domain.scrap.usecase

import org.bmsk.lifemash.model.Article
import org.bmsk.lifemash.domain.scrap.repository.ScrapRepository
import javax.inject.Inject

interface AddScrapUseCase {
    suspend operator fun invoke(article: Article)
}

internal class AddScrapUseCaseImpl @Inject constructor(
    private val scrapRepository: ScrapRepository
) : AddScrapUseCase {
    override suspend operator fun invoke(article: Article) {
        scrapRepository.addScrappedArticle(article)
    }
}
