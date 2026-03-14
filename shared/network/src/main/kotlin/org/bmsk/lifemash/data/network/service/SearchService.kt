package org.bmsk.lifemash.data.network.service

import org.bmsk.lifemash.data.network.response.SearchApiResponse
import org.bmsk.lifemash.data.network.response.SearchRequestBody
import retrofit2.http.Body
import retrofit2.http.POST

internal interface SearchService {
    @POST("search")
    suspend fun search(@Body body: SearchRequestBody): SearchApiResponse
}
