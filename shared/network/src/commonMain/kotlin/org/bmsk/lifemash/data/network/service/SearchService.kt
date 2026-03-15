package org.bmsk.lifemash.data.network.service

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.bmsk.lifemash.data.network.BASE_URL_SEARCH
import org.bmsk.lifemash.data.network.response.SearchApiResponse
import org.bmsk.lifemash.data.network.response.SearchRequestBody

internal class SearchService(private val client: HttpClient) {
    suspend fun search(body: SearchRequestBody): SearchApiResponse {
        return client.post("${BASE_URL_SEARCH}search") {
            contentType(ContentType.Application.Json)
            setBody(body)
        }.body()
    }
}
