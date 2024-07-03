package org.bmsk.lifemash.feature.scrap.usecase

import org.bmsk.lifemash.core.model.NewsModel
import org.bmsk.lifemash.core.repo.scrap.api.ScrapNewsRepository
import javax.inject.Inject

internal interface DeleteScrapNewsUseCase {
    operator fun invoke(newsModel: NewsModel)
}

internal class DeleteScrapNewsUseCaseImpl @Inject constructor(
    private val repository: ScrapNewsRepository
) : DeleteScrapNewsUseCase {
    override fun invoke(newsModel: NewsModel) {
        repository.deleteNewsFromDB(newsModel)
    }
}