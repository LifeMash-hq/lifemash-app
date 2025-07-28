package org.bmsk.lifemash.feature.scrap

import kotlinx.collections.immutable.PersistentList
import org.bmsk.lifemash.core.model.NewsModel

internal sealed interface ScrapUiState {
    data object NewsLoading : ScrapUiState

    data class NewsLoaded(
        val scraps: PersistentList<NewsModel>
    ) : ScrapUiState

    data object NewsEmpty : ScrapUiState

    data class Error(val throwable: Throwable) : ScrapUiState
}