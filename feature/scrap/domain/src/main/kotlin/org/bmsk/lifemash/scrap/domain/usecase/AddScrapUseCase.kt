package org.bmsk.lifemash.scrap.domain.usecase

import org.bmsk.lifemash.model.Article
import org.bmsk.lifemash.scrap.domain.repository.ScrapRepository
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
