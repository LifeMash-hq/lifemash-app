package org.bmsk.lifemash.feature.topic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest

internal object TopicRoute {
    const val ROUTE = "topic"

    @Composable
    operator fun invoke(
        onClickNews: (url: String) -> Unit,
        onClickScrapPage: () -> Unit,
        onShowErrorSnackbar: (throwable: Throwable) -> Unit,
        viewModel: TopicViewModel = hiltViewModel(),
    ) {
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        LaunchedEffect(true) { viewModel.errorFlow.collectLatest(onShowErrorSnackbar) }
        TopicScreen(
            isLoading = uiState.isLoading,
            newsList = uiState.newsList,
            currentSection = uiState.currentSection,
            onClickSection = viewModel::fetchNews,
            onSearchClick = viewModel::fetchNewsSearchResults,
            onClickNews = onClickNews,
            onClickScrap = viewModel::scrapNews,
            onClickScrapPage = onClickScrapPage
        )
    }
}
