package org.bmsk.lifemash.feature.scrap

import kotlinx.collections.immutable.PersistentList
import org.bmsk.lifemash.model.Article
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val ArticleDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    .withZone(ZoneId.systemDefault())

internal sealed interface ScrapUiState {
    data object NewsLoading : ScrapUiState

    data class NewsLoaded(
        val scraps: PersistentList<ScrapArticleUi>
    ) : ScrapUiState

    data object NewsEmpty : ScrapUiState

    data class Error(val throwable: Throwable) : ScrapUiState
}

internal data class ScrapArticleUi(
    val article: Article,
    val publishedAtRelative: String,
) {
    companion object {
        fun from(article: Article): ScrapArticleUi {
            return ScrapArticleUi(
                article = article,
                publishedAtRelative = ArticleDateFormatter.format(article.publishedAt),
            )
        }
    }
}
