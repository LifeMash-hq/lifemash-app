package org.bmsk.lifemash.data.network.service

import org.bmsk.lifemash.data.network.response.NewsRss
import retrofit2.http.GET
import retrofit2.http.Query

interface SbsNewsService {
    @GET("news/SectionRssFeed.do")
    suspend fun getNews(
        @Query("sectionId") sectionId: String = "02",
        @Query("plink") plink: String = "RSSREADER",
    ): NewsRss
}
