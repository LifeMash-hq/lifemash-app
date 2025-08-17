package org.bmsk.lifemash.core.repo.search.impl

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.invoke
import org.bmsk.lifemash.core.model.NewsModel
import org.bmsk.lifemash.core.model.section.LifeMashCategory
import org.bmsk.lifemash.core.model.section.SBSSection
import org.bmsk.lifemash.core.network.response.LegacyLifeMashArticleResponse
import org.bmsk.lifemash.core.network.response.NewsItem
import org.bmsk.lifemash.core.network.service.GoogleNewsService
import org.bmsk.lifemash.core.network.service.LifeMashFirebaseService
import org.bmsk.lifemash.core.network.service.SbsNewsService
import org.bmsk.lifemash.core.repo.search.api.NewsRepository
import org.bmsk.lifemash.core.repo.search.impl.transform.toModel
import javax.inject.Inject

internal class NewsRepositoryImpl @Inject constructor(
    private val sbsNewsService: SbsNewsService,
    private val googleNewsService: GoogleNewsService,
    private val lifeMashFirebaseService: LifeMashFirebaseService,
) : NewsRepository {

    var count = 0

    override suspend fun getSbsNews(section: SBSSection): List<NewsModel> {
        runCatching {
            if (count == 0) {
                lifeMashFirebaseService.getLatestNews().also {
                    Log.e("NewsRepositoryImpl", it.toString())
                    Log.e("NewsRepositoryImpl", it.size.toString())
                }
                count++
            }
        }.onFailure {
            Log.e("NewsRepositoryImpl", it.stackTraceToString())
        }
        return Dispatchers.IO {
            sbsNewsService
                .getNews(section.id).channel.items
                ?.map(NewsItem::toModel)
                ?: emptyList()
        }
    }

    override suspend fun getGoogleNews(query: String): List<NewsModel> {
        return Dispatchers.IO {
            googleNewsService.search(query).channel.items
                ?.map(NewsItem::toModel)
                ?: emptyList()
        }
    }

    override suspend fun getLifeMashNews(category: LifeMashCategory): List<NewsModel> {
        return Dispatchers.IO {
            lifeMashFirebaseService.getLatestNews(category = category)
                .map(LegacyLifeMashArticleResponse::toModel)
        }
    }
}
