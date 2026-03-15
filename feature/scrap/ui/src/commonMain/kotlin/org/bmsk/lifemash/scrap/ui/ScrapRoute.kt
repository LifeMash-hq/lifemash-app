package org.bmsk.lifemash.scrap.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.runtime.collectAsState

@Composable
internal fun ScrapRouteScreen(
    onClickNews: (url: String) -> Unit,
    onShowErrorSnackbar: (Throwable?) -> Unit,
    viewModel: ScrapViewModel = koinViewModel(),
) {
    val scrapUiState by viewModel.uiState.collectAsState()

    ScrapNewsScreen(
        scrapUiState = scrapUiState,
        onClickNews = onClickNews,
        deleteScrapNews = viewModel::deleteScrapNews
    )
}
