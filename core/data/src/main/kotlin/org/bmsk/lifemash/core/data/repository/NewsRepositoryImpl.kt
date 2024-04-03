package org.bmsk.lifemash.core.data.repository

import org.bmsk.lifemash.core.data.asDomain
import org.bmsk.lifemash.core.domain.repository.NewsRepository
import org.bmsk.lifemash.core.model.NewsModel
import org.bmsk.lifemash.core.model.section.SbsSection
import org.bmsk.lifemash.core.network.service.NewsClient
import javax.inject.Inject

internal class NewsRepositoryImpl @Inject constructor(
    private val newsClient: NewsClient,
) : NewsRepository {
    override suspend fun getSbsNews(section: SbsSection): List<NewsModel> {
        return runCatching {
            newsClient.getSbsNews(section).channel.items ?: emptyList()
        }.fold(
            onSuccess = { it.asDomain() },
            onFailure = { emptyList() },
        )
    }

    override suspend fun getGoogleNews(query: String): List<NewsModel> {
        return runCatching {
            newsClient.getGoogleNews(query).channel.items ?: emptyList()
        }.fold(
            onSuccess = { it.asDomain() },
            onFailure = { emptyList() },
        )
    }
}
