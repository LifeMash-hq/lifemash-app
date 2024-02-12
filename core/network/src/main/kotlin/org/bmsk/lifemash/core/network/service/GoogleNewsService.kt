package org.bmsk.lifemash.core.network.service

import org.bmsk.lifemash.core.network.model.NewsRss
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

internal interface GoogleNewsService {
    @GET("rss/search?hl=ko&gl=KR&ceid=KR%3Ako")
    suspend fun search(
        @Query("q") query: String,
    ): Response<NewsRss>
}
