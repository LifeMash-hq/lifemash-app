package org.bmsk.lifemash.feature.topic.usecase

import org.bmsk.lifemash.core.model.NewsModel
import org.bmsk.lifemash.core.model.section.LifeMashCategory
import org.bmsk.lifemash.core.repo.search.api.NewsRepository
import javax.inject.Inject

internal interface GetLifeMashNewsUseCase {
    suspend operator fun invoke(category: LifeMashCategory): List<NewsModel>
}

internal class GetLifeMashNewsUseCaseImpl @Inject constructor(
    private val repository: NewsRepository,
) : GetLifeMashNewsUseCase {
    override suspend fun invoke(category: LifeMashCategory): List<NewsModel> {
        return repository.getLifeMashNews(category)
    }
}