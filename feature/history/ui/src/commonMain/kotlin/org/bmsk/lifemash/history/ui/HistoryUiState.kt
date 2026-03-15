package org.bmsk.lifemash.history.ui

import kotlinx.collections.immutable.PersistentList
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.bmsk.lifemash.model.Article

sealed interface HistoryUiState {
    data object Loading : HistoryUiState
    data class Loaded(val articles: PersistentList<HistoryArticleUi>) : HistoryUiState
    data object Empty : HistoryUiState
    data class Error(val throwable: Throwable) : HistoryUiState
}

data class HistoryArticleUi(
    val article: Article,
    val publishedAtFormatted: String,
) {
    companion object {
        fun from(article: Article): HistoryArticleUi {
            val local = article.publishedAt.toLocalDateTime(TimeZone.currentSystemDefault())
            val formatted = "${local.year.toString().padStart(4, '0')}-${local.monthNumber.toString().padStart(2, '0')}-${local.dayOfMonth.toString().padStart(2, '0')} ${local.hour.toString().padStart(2, '0')}:${local.minute.toString().padStart(2, '0')}"
            return HistoryArticleUi(
                article = article,
                publishedAtFormatted = formatted,
            )
        }
    }
}
