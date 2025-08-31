package org.bmsk.lifemash.feature.scrap

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

internal object ScrapRoute {
    const val ROUTE = "scrap"

    @Composable
    operator fun invoke(
        onClickNews: (url: String) -> Unit,
        onShowErrorSnackbar: (Throwable?) -> Unit,
        viewModel: ScrapViewModel = hiltViewModel(),
    ) {
        val scrapUiState by viewModel.uiState.collectAsStateWithLifecycle()

        ScrapNewsScreen(
            scrapUiState = scrapUiState,
            onClickNews = onClickNews,
            deleteScrapNews = viewModel::deleteScrapNews
        )
    }
}