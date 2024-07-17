package org.bmsk.lifemash.core.repo.search.api

import org.bmsk.lifemash.core.model.NewsModel
import org.bmsk.lifemash.core.model.section.SBSSection

interface NewsRepository {
    suspend fun getSbsNews(section: SBSSection): List<NewsModel>
    suspend fun getGoogleNews(query: String): List<NewsModel>
}
