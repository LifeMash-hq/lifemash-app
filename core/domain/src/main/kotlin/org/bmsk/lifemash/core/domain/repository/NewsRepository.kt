package org.bmsk.lifemash.core.domain.repository

import org.bmsk.lifemash.core.model.NewsModel
import org.bmsk.lifemash.core.model.section.SbsSection

interface NewsRepository {
    suspend fun getSbsNews(section: SbsSection): List<NewsModel>
    suspend fun getGoogleNews(query: String): List<NewsModel>
}
