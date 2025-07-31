package org.bmsk.lifemash.feature.scrap

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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

        LaunchedEffect(scrapUiState) {
            when (scrapUiState) {
                is ScrapUiState.Error -> Unit
                ScrapUiState.NewsEmpty -> Unit
                is ScrapUiState.NewsLoaded -> Unit
                ScrapUiState.NewsLoading -> {
                    viewModel.getScrapNews()
                }
            }
        }
        ScrapNewsScreen(
            scrapUiState = scrapUiState,
            onClickNews = onClickNews,
            deleteScrapNews = viewModel::deleteScrapNews
        )
    }
}
