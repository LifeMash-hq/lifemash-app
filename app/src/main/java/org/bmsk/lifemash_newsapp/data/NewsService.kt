package org.bmsk.lifemash_newsapp.data

import org.bmsk.lifemash_newsapp.data.model.NewsRss
import retrofit2.Call
import retrofit2.http.GET

interface NewsService {
    @GET("rss?hl=ko&gl=KR&ceid=KR:ko")
    fun getMainFeed(): Call<NewsRss>
}