package org.bmsk.lifemash_newsapp.data

import org.bmsk.lifemash_newsapp.data.model.NewsRss
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleNewsService {
    @GET("rss/search?hl=ko&gl=KR&ceid=KR%3Ako")
    fun search(
        @Query("q") query: String
    ): Call<NewsRss>
}