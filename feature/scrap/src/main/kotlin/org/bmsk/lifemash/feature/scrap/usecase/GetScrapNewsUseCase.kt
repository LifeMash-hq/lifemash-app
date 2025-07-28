package org.bmsk.lifemash.feature.scrap.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.invoke
import org.bmsk.lifemash.core.model.NewsModel
import org.bmsk.lifemash.core.repo.scrap.api.ScrapNewsRepository
import javax.inject.Inject

internal interface GetScrapNewsUseCase {
    suspend operator fun invoke(): List<NewsModel>
}

internal class GetScrapNewsUseCaseImpl @Inject constructor(
    private val repository: ScrapNewsRepository
) : GetScrapNewsUseCase {
    override suspend fun invoke(): List<NewsModel> = Dispatchers.IO { repository.getNewsFromDB() }
}