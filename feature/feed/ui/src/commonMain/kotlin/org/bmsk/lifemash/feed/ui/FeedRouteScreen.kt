package org.bmsk.lifemash.feed.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun FeedRouteScreen(
    onShowErrorSnackbar: (Throwable?) -> Unit,
    onNavigateToEventDetail: (String) -> Unit = {},
    viewModel: FeedViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadFeed()
    }

    FeedScreen(
        uiState = uiState,
        selectedFilter = selectedFilter,
        onFilterSelect = viewModel::selectFilter,
        onRetry = viewModel::loadFeed,
        onPostClick = onNavigateToEventDetail,
    )
}
