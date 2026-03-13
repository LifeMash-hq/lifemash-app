package org.bmsk.lifemash.domain.scrap.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.bmsk.lifemash.domain.core.model.ArticleId
import org.bmsk.lifemash.domain.scrap.repository.ScrapRepository
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
