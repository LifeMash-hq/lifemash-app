package org.bmsk.lifemash.core.network.service

import org.bmsk.lifemash.core.network.model.NewsRss
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SbsNewsService {
    @GET("news/SectionRssFeed.do")
    suspend fun getNews(
        @Query("sectionId")
        sectionId: String = "02",

        @Query("plink")
        plink: String = "RSSREADER",
    ): Response<NewsRss>
}
