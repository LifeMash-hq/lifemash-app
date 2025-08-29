package org.bmsk.lifemash.feature.scrap

import kotlinx.collections.immutable.PersistentList
import org.bmsk.lifemash.domain.core.model.Article

internal sealed interface ScrapUiState {
    data object NewsLoading : ScrapUiState

    data class NewsLoaded(
        val scraps: PersistentList<ScrapUiModel>
    ) : ScrapUiState

    data object NewsEmpty : ScrapUiState

    data class Error(val throwable: Throwable) : ScrapUiState
}

internal data class ScrapUiModel(
    val id: String,
    val title: String,
    val publisher: String,
    val publishedAtRelative: String,
    val link: String,
    val imageUrl: String?
) {
    companion object {
        // A simple mapper for now. A more sophisticated one could be added.
        fun from(article: Article): ScrapUiModel {
            return ScrapUiModel(
                id = article.id.value,
                title = article.title,
                publisher = article.publisher.name,
                publishedAtRelative = article.publishedAt.toString(), // Simplified for now
                link = article.link.value,
                imageUrl = article.image?.value
            )
        }
    }
}
