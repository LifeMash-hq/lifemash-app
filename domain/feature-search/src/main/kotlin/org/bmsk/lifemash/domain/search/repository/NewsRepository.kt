package org.bmsk.lifemash.domain.search.repository

import org.bmsk.lifemash.core.model.section.SBSSection
import org.bmsk.lifemash.domain.core.model.Article

interface NewsRepository {
    suspend fun getSbsNews(section: SBSSection): List<Article>
    suspend fun getGoogleNews(query: String): List<Article>
}