package org.bmsk.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.bmsk.data.asDomain
import org.bmsk.domain.repository.NewsRepository
import org.bmsk.model.NewsModel
import org.bmsk.model.section.SbsSection
import org.bmsk.network.service.NewsClient
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class NewsRepositoryImpl @Inject constructor(
    private val newsClient: NewsClient,
    private val ioDispatcher: CoroutineContext,
) : NewsRepository {
    override suspend fun getSbsNews(section: SbsSection): Flow<List<NewsModel>> {
        return flow {
            val response = newsClient.getSbsNews(section)
            if (response.isSuccessful) {
                val newsList = (response.body()?.channel?.items ?: emptyList()).asDomain()
                emit(newsList)
            } else {
                emit(emptyList())
            }
        }.flowOn(ioDispatcher)
    }

    override suspend fun getGoogleNews(query: String): Flow<List<NewsModel>> {
        return flow {
            val response = newsClient.getGoogleNews(query)
            if (response.isSuccessful) {
                val newsList = response.body()?.channel?.items ?: emptyList()
                emit(newsList.asDomain())
            } else {
                emit(emptyList())
            }
        }.flowOn(ioDispatcher)
    }
}
