package org.bmsk.lifemash.core.repo.search.impl

import org.bmsk.lifemash.core.model.NewsModel
import org.bmsk.lifemash.core.model.section.SBSSection
import org.bmsk.lifemash.core.network.model.NewsItem
import org.bmsk.lifemash.core.network.service.NewsClient
import org.bmsk.lifemash.core.repo.search.api.NewsRepository
import javax.inject.Inject

internal class NewsRepositoryImpl @Inject constructor(
    private val newsClient: NewsClient,
) : NewsRepository {
    override suspend fun getSbsNews(section: SBSSection): List<NewsModel> {
        return runCatching {
            newsClient.getSbsNews(section).channel.items ?: emptyList()
        }.fold(
            onSuccess = List<NewsItem>::asModel,
            onFailure = { emptyList() },
        )
    }

    override suspend fun getGoogleNews(query: String): List<NewsModel> {
        return runCatching {
            newsClient.getGoogleNews(query).channel.items ?: emptyList()
        }.fold(
            onSuccess = List<NewsItem>::asModel,
            onFailure = { emptyList() },
        )
    }
}
