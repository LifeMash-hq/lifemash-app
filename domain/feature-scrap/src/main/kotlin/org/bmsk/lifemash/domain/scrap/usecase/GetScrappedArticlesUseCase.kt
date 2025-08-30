package org.bmsk.lifemash.domain.scrap.usecase

import org.bmsk.lifemash.domain.core.model.Article
import org.bmsk.lifemash.domain.scrap.repository.ScrapRepository

interface GetScrappedArticlesUseCase {
    suspend operator fun invoke(): List<Article>
}

internal class GetScrappedArticlesUseCaseImpl(
    private val scrapRepository: ScrapRepository
) : GetScrappedArticlesUseCase {
    override suspend operator fun invoke(): List<Article> {
        return scrapRepository.getScrappedArticles()
    }
}
