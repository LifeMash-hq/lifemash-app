package org.bmsk.lifemash.scrap.ui

import kotlinx.collections.immutable.PersistentList
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.bmsk.lifemash.model.Article

sealed interface ScrapUiState {
    data object NewsLoading : ScrapUiState

    data class NewsLoaded(
        val scraps: PersistentList<ScrapArticleUi>
    ) : ScrapUiState

    data object NewsEmpty : ScrapUiState

    data class Error(val throwable: Throwable) : ScrapUiState
}

data class ScrapArticleUi(
    val article: Article,
    val publishedAtRelative: String,
) {
    companion object {
        fun from(article: Article): ScrapArticleUi {
            return ScrapArticleUi(
                article = article,
                publishedAtRelative = article.publishedAt
                    .toLocalDateTime(TimeZone.currentSystemDefault())
                    .let { "${it.year.toString().padStart(4, '0')}-${it.monthNumber.toString().padStart(2, '0')}-${it.dayOfMonth.toString().padStart(2, '0')} ${it.hour.toString().padStart(2, '0')}:${it.minute.toString().padStart(2, '0')}" },
            )
        }
    }
}
