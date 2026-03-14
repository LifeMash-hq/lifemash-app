package org.bmsk.lifemash.data.search

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.invoke
import org.bmsk.lifemash.data.network.response.NewsItem
import org.bmsk.lifemash.data.network.service.GoogleNewsService
import org.bmsk.lifemash.model.Article
import org.bmsk.lifemash.model.ArticleId
import org.bmsk.lifemash.model.ArticleUrl
import org.bmsk.lifemash.model.Publisher
import org.bmsk.lifemash.domain.search.repository.NewsRepository
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Locale
import javax.inject.Inject

internal class NewsRepositoryImpl @Inject constructor(
    private val googleNewsService: GoogleNewsService,
) : NewsRepository {

    override suspend fun getGoogleNews(query: String): List<Article> {
        return Dispatchers.IO {
            googleNewsService.search(query).channel.items
                ?.map { it.toDomain() }
                ?: emptyList()
        }
    }

    private fun NewsItem.toDomain(): Article {
        val nonNullLink = requireNotNull(link) { "NewsItem link cannot be null" }
        val publishedInstant =
            pubDate?.let { parseRssDate(it).toInstant() } ?: Instant.EPOCH
        return Article(
            id = ArticleId.from(nonNullLink),
            publisher = Publisher.unknown,
            title = title ?: "",
            summary = "",
            link = ArticleUrl.from(nonNullLink),
            image = null,
            publishedAt = publishedInstant,
            categories = emptyList(),
        )
    }

    private fun parseRssDate(input: String): java.util.Date {
        val parser = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH)
        return parser.parse(input) as java.util.Date
    }
}
