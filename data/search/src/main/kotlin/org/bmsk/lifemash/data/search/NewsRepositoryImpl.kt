package org.bmsk.lifemash.data.search

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.invoke
import org.bmsk.lifemash.core.model.DateParser
import org.bmsk.lifemash.core.model.section.SBSSection
import org.bmsk.lifemash.core.network.response.NewsItem
import org.bmsk.lifemash.core.network.service.GoogleNewsService
import org.bmsk.lifemash.core.network.service.SbsNewsService
import org.bmsk.lifemash.domain.core.model.Article
import org.bmsk.lifemash.domain.core.model.ArticleId
import org.bmsk.lifemash.domain.core.model.ArticleUrl
import org.bmsk.lifemash.domain.core.model.Publisher
import org.bmsk.lifemash.domain.search.repository.NewsRepository
import java.time.Instant
import javax.inject.Inject

internal class NewsRepositoryImpl @Inject constructor(
    private val sbsNewsService: SbsNewsService,
    private val googleNewsService: GoogleNewsService,
) : NewsRepository {

    override suspend fun getSbsNews(section: SBSSection): List<Article> {
        return Dispatchers.IO {
            sbsNewsService
                .getNews(section.id).channel.items
                ?.map { it.toDomain() }
                ?: emptyList()
        }
    }

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
            pubDate?.let { DateParser.parseDate(it).toInstant() } ?: Instant.EPOCH
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
}
