package org.bmsk.lifemash.data.network.service

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import org.bmsk.lifemash.data.network.BASE_URL_SBS
import org.bmsk.lifemash.data.network.response.NewsRss
import org.bmsk.lifemash.data.network.xml.RssParser

internal class SbsNewsService(private val client: HttpClient) {
    suspend fun getNews(sectionId: String = "02", plink: String = "RSSREADER"): NewsRss {
        val response = client.get("${BASE_URL_SBS}news/SectionRssFeed.do") {
            parameter("sectionId", sectionId)
            parameter("plink", plink)
        }
        return RssParser.parse(response.bodyAsText())
    }
}
