package org.bmsk.lifemash.explore.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun ExploreRouteScreen(
    onShowErrorSnackbar: (Throwable?) -> Unit,
    viewModel: ExploreViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    ExploreScreen(uiState = uiState, onSearch = viewModel::search)
}
