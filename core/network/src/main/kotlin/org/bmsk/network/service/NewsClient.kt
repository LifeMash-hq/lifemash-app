package org.bmsk.network.service

import org.bmsk.model.section.SbsSection
import org.bmsk.network.model.NewsRss
import retrofit2.Response
import javax.inject.Inject

class NewsClient @Inject constructor(
    private val googleNewsService: GoogleNewsService,
    private val sbsNewsService: SbsNewsService,
){
    suspend fun getSbsNews(
        sectionId: String = SbsSection.SECTION_ECONOMICS,
        plink: String = "RSSREADER"
    ): Response<NewsRss> {
        return sbsNewsService.getNews(
            sectionId = sectionId,
            plink = plink
        )
    }

    suspend fun getGoogleNews(
        query: String,
    ): Response<NewsRss> {
        return googleNewsService.search(
            query = query
        )
    }
}