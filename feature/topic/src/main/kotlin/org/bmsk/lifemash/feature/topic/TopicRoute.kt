package org.bmsk.lifemash.feature.topic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

internal object TopicRoute {
    const val ROUTE = "topic"

    @Composable
    operator fun invoke(
        onNewsClick: (url: String) -> Unit,
        onScrapPageClick: () -> Unit,
        onShowErrorSnackbar: (throwable: Throwable) -> Unit,
        viewModel: TopicViewModel = hiltViewModel(),
    ) {
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        LaunchedEffect(uiState.selectedSection) {
            viewModel.getNews(uiState.selectedSection)
        }

        TopicScreen(
            uiState = uiState,
            onQueryChange = viewModel::setQuery,
            onSectionClick = viewModel::setSection,
            onSearchClick = viewModel::getGoogleNews,
            onNewsClick = onNewsClick,
            onScrapNewsClick = viewModel::scrapNews,
            onScrapPageClick = onScrapPageClick,
            onNewsOverflowMenuClick = viewModel::setSelectedOverflowMenuNews,
            onDismissSelectedNews = {
                viewModel.setSelectedOverflowMenuNews(null)
                viewModel.setScrapingUiState()
            }
        )
    }
}
