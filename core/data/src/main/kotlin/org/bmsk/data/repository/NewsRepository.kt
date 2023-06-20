package org.bmsk.data.repository

import kotlinx.coroutines.flow.Flow
import org.bmsk.model.NewsModel
import org.bmsk.model.section.SbsSection

interface NewsRepository {
    suspend fun getSbsNews(section: SbsSection): Flow<List<NewsModel>>
    suspend fun getGoogleNews(query: String): Flow<List<NewsModel>>
}