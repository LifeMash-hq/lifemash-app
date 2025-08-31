package org.bmsk.lifemash.domain.scrap.usecase

import kotlinx.coroutines.flow.Flow
import org.bmsk.lifemash.domain.core.model.Article
import org.bmsk.lifemash.domain.scrap.repository.ScrapRepository

interface GetScrappedArticlesUseCase {
    operator fun invoke(): Flow<List<Article>>
}

internal class GetScrappedArticlesUseCaseImpl(
    private val scrapRepository: ScrapRepository
) : GetScrappedArticlesUseCase {
    override operator fun invoke(): Flow<List<Article>> {
        return scrapRepository.getScrappedArticles()
    }
}
