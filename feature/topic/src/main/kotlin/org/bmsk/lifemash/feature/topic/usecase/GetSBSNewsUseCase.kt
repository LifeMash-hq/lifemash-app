package org.bmsk.lifemash.feature.topic.usecase

import org.bmsk.lifemash.core.model.NewsModel
import org.bmsk.lifemash.core.model.section.SBSSection
import org.bmsk.lifemash.core.repo.search.api.NewsRepository
import javax.inject.Inject

internal interface GetSBSNewsUseCase {
    suspend operator fun invoke(section: SBSSection): List<NewsModel>
}

internal class GetSBSNewsUseCaseImpl @Inject constructor(
    private val repository: NewsRepository
) : GetSBSNewsUseCase {
    override suspend fun invoke(section: SBSSection): List<NewsModel> =
        repository.getSbsNews(section)
}
