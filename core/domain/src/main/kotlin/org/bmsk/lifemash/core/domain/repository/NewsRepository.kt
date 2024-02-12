package org.bmsk.lifemash.core.domain.repository

import kotlinx.coroutines.flow.Flow
import org.bmsk.lifemash.core.model.NewsModel
import org.bmsk.lifemash.core.model.section.SbsSection

interface NewsRepository {
    suspend fun getSbsNews(section: SbsSection): Flow<List<NewsModel>>
    suspend fun getGoogleNews(query: String): Flow<List<NewsModel>>
}
