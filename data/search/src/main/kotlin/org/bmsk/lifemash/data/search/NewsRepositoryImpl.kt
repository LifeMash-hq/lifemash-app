package org.bmsk.lifemash.data.search

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.invoke
import org.bmsk.lifemash.core.model.section.SBSSection
import org.bmsk.lifemash.core.network.service.GoogleNewsService
import org.bmsk.lifemash.core.network.service.LifeMashFirebaseService
import org.bmsk.lifemash.core.network.service.SbsNewsService
import org.bmsk.lifemash.data.search.transform.toDomain
import org.bmsk.lifemash.domain.core.model.Article
import org.bmsk.lifemash.domain.search.repository.NewsRepository
import javax.inject.Inject

internal class NewsRepositoryImpl @Inject constructor(
    private val sbsNewsService: SbsNewsService,
    private val googleNewsService: GoogleNewsService,
    private val lifeMashFirebaseService: LifeMashFirebaseService,
) : NewsRepository {

    var count = 0 // This is a temporary variable, should be removed later

    override suspend fun getSbsNews(section: SBSSection): List<Article> {
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
                ?.map { it.toDomain() } // Use new mapper
                ?: emptyList()
        }
    }

    override suspend fun getGoogleNews(query: String): List<Article> {
        return Dispatchers.IO {
            googleNewsService.search(query).channel.items
                ?.map { it.toDomain() } // Use new mapper
                ?: emptyList()
        }
    }
}