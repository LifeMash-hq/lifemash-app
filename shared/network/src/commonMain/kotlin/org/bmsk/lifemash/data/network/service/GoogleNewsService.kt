package org.bmsk.lifemash.data.network.service

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import org.bmsk.lifemash.data.network.BASE_URL_GOOGLE
import org.bmsk.lifemash.data.network.response.NewsRss
import org.bmsk.lifemash.data.network.xml.RssParser

internal class GoogleNewsService(private val client: HttpClient) {
    suspend fun search(query: String): NewsRss {
        val response = client.get("${BASE_URL_GOOGLE}rss/search") {
            parameter("q", query)
            parameter("hl", "ko")
            parameter("gl", "KR")
            parameter("ceid", "KR:ko")
        }
        return RssParser.parse(response.bodyAsText())
    }
}
