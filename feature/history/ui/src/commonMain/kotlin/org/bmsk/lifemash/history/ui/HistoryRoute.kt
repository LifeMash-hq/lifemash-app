package org.bmsk.lifemash.history.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.runtime.collectAsState

@Composable
internal fun HistoryRouteScreen(
    onClickArticle: (url: String) -> Unit,
    onShowErrorSnackbar: (Throwable?) -> Unit,
    viewModel: HistoryViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    HistoryScreen(
        uiState = uiState,
        onClickArticle = onClickArticle,
        onShowErrorSnackbar = onShowErrorSnackbar,
    )
}
