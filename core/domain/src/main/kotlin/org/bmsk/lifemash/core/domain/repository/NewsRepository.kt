package org.bmsk.lifemash.core.domain.repository

import kotlinx.coroutines.flow.Flow
import org.bmsk.core.model.NewsModel
import org.bmsk.core.model.section.SbsSection

interface NewsRepository {
    suspend fun getSbsNews(section: SbsSection): Flow<List<NewsModel>>
    suspend fun getGoogleNews(query: String): Flow<List<NewsModel>>
}
