package org.bmsk.lifemash.feature.scrap

import org.bmsk.lifemash.core.model.NewsModel

internal sealed interface ScrapUiState {
    data object Loading : ScrapUiState

    data class Success(
        val scraps: List<NewsModel>
    ) : ScrapUiState

    data object Fail : ScrapUiState
}