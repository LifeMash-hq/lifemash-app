package org.bmsk.lifemash.feed.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun FeedRouteScreen(
    onShowErrorSnackbar: (Throwable?) -> Unit,
    viewModel: FeedViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadFeed()
    }

    FeedScreen(uiState = uiState, onRetry = viewModel::loadFeed)
}
