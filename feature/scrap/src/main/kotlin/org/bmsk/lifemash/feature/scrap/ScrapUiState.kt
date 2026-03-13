package org.bmsk.lifemash.feature.scrap

import kotlinx.collections.immutable.PersistentList
import org.bmsk.lifemash.domain.core.model.Article
import org.bmsk.lifemash.core.common.ArticleDateFormatter

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
