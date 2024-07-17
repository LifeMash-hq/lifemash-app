package org.bmsk.lifemash.core.network.service

import org.bmsk.lifemash.core.model.section.SBSSection
import org.bmsk.lifemash.core.network.model.NewsRss
import javax.inject.Inject

interface NewsClient {
    suspend fun getSbsNews(
        section: SBSSection = SBSSection.ECONOMICS,
        plink: String = "RSSREADER",
    ): NewsRss

    suspend fun getGoogleNews(
        query: String,
    ): NewsRss
}

internal class NewsClientImpl @Inject constructor(
    private val googleNewsService: GoogleNewsService,
    private val sbsNewsService: SbsNewsService,
) : NewsClient {
    override suspend fun getSbsNews(
        section: SBSSection,
        plink: String,
    ): NewsRss {
        return sbsNewsService.getNews(
            sectionId = section.id,
            plink = plink,
        )
    }

    override suspend fun getGoogleNews(
        query: String,
    ): NewsRss {
        return googleNewsService.search(
            query = query,
        )
    }
}
