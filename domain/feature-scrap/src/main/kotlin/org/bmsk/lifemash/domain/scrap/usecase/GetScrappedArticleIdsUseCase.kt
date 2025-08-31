package org.bmsk.lifemash.domain.scrap.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.bmsk.lifemash.domain.core.model.ArticleId
import org.bmsk.lifemash.domain.scrap.repository.ScrapRepository
import javax.inject.Inject

class GetScrappedArticleIdsUseCase @Inject constructor(
    private val scrapRepository: ScrapRepository
) {
    operator fun invoke(): Flow<Set<ArticleId>> {
        return scrapRepository.getScrappedArticles().map { articles ->
            articles.map { it.id }.toSet()
        }
    }
}
