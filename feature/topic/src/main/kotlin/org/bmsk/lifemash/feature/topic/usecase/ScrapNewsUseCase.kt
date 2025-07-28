package org.bmsk.lifemash.feature.topic.usecase

import org.bmsk.lifemash.core.model.NewsModel
import org.bmsk.lifemash.core.repo.scrap.api.ScrapNewsRepository
import javax.inject.Inject

internal interface ScrapNewsUseCase {
    suspend operator fun invoke(newsModel: NewsModel)
}

internal class ScrapNewsUseCaseImpl @Inject constructor(
    private val scrapNewsRepository: ScrapNewsRepository
) : ScrapNewsUseCase {
    override suspend fun invoke(newsModel: NewsModel) {
        val isExist = scrapNewsRepository.getNewsByLink(newsModel.link) != null
        if (isExist) return

        scrapNewsRepository.addNewsToDB(newsModel)
    }
}