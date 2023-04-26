package org.bmsk.lifemash_newsapp.data

import org.bmsk.lifemash_newsapp.data.model.NewsRss
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface SbsNewsService {
    @GET("news/SectionRssFeed.do")
    fun getNews(
        @Query("sectionId")
        sectionId: String = "02",

        @Query("plink")
        plink: String = "RSSREADER"
    ): Call<NewsRss>
}