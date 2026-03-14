package org.bmsk.lifemash.core.network.service

import org.bmsk.lifemash.core.network.response.SearchApiResponse
import org.bmsk.lifemash.core.network.response.SearchRequestBody
import retrofit2.http.Body
import retrofit2.http.POST

internal interface SearchService {
    @POST("search")
    suspend fun search(@Body body: SearchRequestBody): SearchApiResponse
}
