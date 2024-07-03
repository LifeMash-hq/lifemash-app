package org.bmsk.lifemash.feature.scrap

import kotlinx.collections.immutable.PersistentList
import org.bmsk.lifemash.core.model.NewsModel

internal sealed interface ScrapUiState {
    data object Loading : ScrapUiState

    data class Success(
        val scraps: PersistentList<NewsModel>
    ) : ScrapUiState

    data object Fail : ScrapUiState
}