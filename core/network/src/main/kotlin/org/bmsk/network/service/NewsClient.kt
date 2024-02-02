package org.bmsk.network.service

import org.bmsk.model.section.SbsSection
import org.bmsk.network.model.NewsRss
import retrofit2.Response
import javax.inject.Inject

class NewsClient @Inject constructor(
    private val googleNewsService: GoogleNewsService,
    private val sbsNewsService: SbsNewsService,
) {
    suspend fun getSbsNews(
        section: SbsSection = SbsSection.ECONOMICS,
        plink: String = "RSSREADER",
    ): Response<NewsRss> {
        return sbsNewsService.getNews(
            sectionId = section.id,
            plink = plink,
        )
    }

    suspend fun getGoogleNews(
        query: String,
    ): Response<NewsRss> {
        return googleNewsService.search(
            query = query,
        )
    }
}
