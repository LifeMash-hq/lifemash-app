package org.bmsk.lifemash.core.domain.usecase

import org.bmsk.lifemash.core.domain.repository.NewsRepository
import org.bmsk.lifemash.core.model.NewsModel
import org.bmsk.lifemash.core.model.section.SbsSection
import javax.inject.Inject

class NewsUseCase @Inject constructor(
    private val newsRepository: NewsRepository,
) {
    suspend fun getSbsNews(section: SbsSection): List<NewsModel> {
        return newsRepository.getSbsNews(section)
    }

    suspend fun getGoogleNews(query: String): List<NewsModel> {
        return newsRepository.getGoogleNews(query)
    }
}
