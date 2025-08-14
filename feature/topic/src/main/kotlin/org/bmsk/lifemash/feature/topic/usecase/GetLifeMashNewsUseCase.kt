package org.bmsk.lifemash.feature.topic.usecase

import org.bmsk.lifemash.core.model.NewsModel
import org.bmsk.lifemash.core.model.section.LifeMashSection
import org.bmsk.lifemash.core.repo.search.api.NewsRepository
import javax.inject.Inject

internal interface GetLifeMashNewsUseCase {
    suspend operator fun invoke(section: LifeMashSection): List<NewsModel>
}

internal class GetLifeMashNewsUseCaseImpl @Inject constructor(
    private val repository: NewsRepository,
) : GetLifeMashNewsUseCase {
    override suspend fun invoke(section: LifeMashSection): List<NewsModel> {
        return repository.getLifeMashNews(section)
    }
}