package org.bmsk.lifemash.domain.scrap.usecase

import kotlinx.coroutines.flow.Flow
import org.bmsk.lifemash.domain.core.model.Article
import org.bmsk.lifemash.domain.scrap.repository.ScrapRepository
import javax.inject.Inject

interface GetScrappedArticlesUseCase {
    operator fun invoke(): Flow<List<Article>>
}

internal class GetScrappedArticlesUseCaseImpl @Inject constructor(
    private val scrapRepository: ScrapRepository
) : GetScrappedArticlesUseCase {
    override operator fun invoke(): Flow<List<Article>> {
        return scrapRepository.getScrappedArticles()
    }
}
