package org.bmsk.lifemash.scrap.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.bmsk.lifemash.model.Article
import org.bmsk.lifemash.scrap.domain.repository.ScrapRepository
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
