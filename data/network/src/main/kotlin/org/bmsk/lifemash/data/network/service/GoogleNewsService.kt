package org.bmsk.lifemash.data.network.service

import org.bmsk.lifemash.data.network.response.NewsRss
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleNewsService {
    @GET("rss/search?hl=ko&gl=KR&ceid=KR%3Ako")
    suspend fun search(
        @Query("q") query: String,
    ): NewsRss
}
