package org.bmsk.lifemash.feature.topic.usecase

import org.bmsk.lifemash.core.model.NewsModel
import org.bmsk.lifemash.core.repo.search.api.NewsRepository
import javax.inject.Inject

internal interface GetGoogleNewsUseCase {
    suspend operator fun invoke(query: String): List<NewsModel>
}

internal class GetGoogleNewsUseCaseImpl @Inject constructor(
    private val repository: NewsRepository
) : GetGoogleNewsUseCase {
    override suspend fun invoke(query: String): List<NewsModel> =
        repository.getGoogleNews(query)
}