package org.bmsk.lifemash.feature.scrap

import kotlinx.collections.immutable.PersistentList
import org.bmsk.lifemash.domain.core.model.Article
import java.time.ZoneId
import java.time.format.DateTimeFormatter

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
        private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            .withZone(ZoneId.systemDefault())

        fun from(article: Article): ScrapArticleUi {
            return ScrapArticleUi(
                article = article,
                publishedAtRelative = formatter.format(article.publishedAt),
            )
        }
    }
}
