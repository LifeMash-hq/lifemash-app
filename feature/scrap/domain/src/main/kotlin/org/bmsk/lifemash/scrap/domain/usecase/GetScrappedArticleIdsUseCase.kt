package org.bmsk.lifemash.scrap.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.bmsk.lifemash.model.ArticleId
import org.bmsk.lifemash.scrap.domain.repository.ScrapRepository
import javax.inject.Inject

interface GetScrappedArticleIdsUseCase {
    operator fun invoke(): Flow<Set<ArticleId>>
}

internal class GetScrappedArticleIdsUseCaseImpl @Inject constructor(
    private val scrapRepository: ScrapRepository
) : GetScrappedArticleIdsUseCase {
    override operator fun invoke(): Flow<Set<ArticleId>> {
        return scrapRepository.getScrappedArticles().map { articles ->
            articles.map { it.id }.toSet()
        }
    }
}
