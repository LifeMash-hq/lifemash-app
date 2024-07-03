package org.bmsk.lifemash.feature.scrap.usecase

import org.bmsk.lifemash.core.model.NewsModel
import org.bmsk.lifemash.core.repo.scrap.api.ScrapNewsRepository
import javax.inject.Inject

internal interface GetScrapNewsUseCase {
    operator fun invoke(): List<NewsModel>
}

internal class GetScrapNewsUseCaseImpl @Inject constructor(
    private val repository: ScrapNewsRepository
) : GetScrapNewsUseCase {
    override fun invoke(): List<NewsModel> = repository.getNewsFromDB()
}