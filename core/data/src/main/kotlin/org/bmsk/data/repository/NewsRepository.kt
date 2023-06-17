package org.bmsk.data.repository

import kotlinx.coroutines.flow.Flow
import org.bmsk.model.NewsModel

interface NewsRepository {
    suspend fun getSbsNews(sectionId: String): Flow<List<NewsModel>>
    suspend fun getGoogleNews(query: String): Flow<List<NewsModel>>
}