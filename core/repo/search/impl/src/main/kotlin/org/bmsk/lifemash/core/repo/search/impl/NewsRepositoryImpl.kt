package org.bmsk.lifemash.core.repo.search.impl

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.invoke
import org.bmsk.lifemash.core.model.NewsModel
import org.bmsk.lifemash.core.model.section.SBSSection
import org.bmsk.lifemash.core.network.service.NewsClient
import org.bmsk.lifemash.core.repo.search.api.NewsRepository
import javax.inject.Inject

internal class NewsRepositoryImpl @Inject constructor(
    private val newsClient: NewsClient,
) : NewsRepository {
    override suspend fun getSbsNews(section: SBSSection): List<NewsModel> {
        return Dispatchers.IO {
            newsClient.getSbsNews(section).channel.items?.asModel() ?: emptyList()
        }
    }

    override suspend fun getGoogleNews(query: String): List<NewsModel> {
        return Dispatchers.IO {
            newsClient.getGoogleNews(query).channel.items?.asModel() ?: emptyList()
        }
    }
}
