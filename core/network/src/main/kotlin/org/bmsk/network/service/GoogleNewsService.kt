package org.bmsk.network.service

import org.bmsk.network.model.NewsRss
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleNewsService {
    @GET("rss/search?hl=ko&gl=KR&ceid=KR%3Ako")
    suspend fun search(
        @Query("q") query: String,
    ): Response<NewsRss>
}
