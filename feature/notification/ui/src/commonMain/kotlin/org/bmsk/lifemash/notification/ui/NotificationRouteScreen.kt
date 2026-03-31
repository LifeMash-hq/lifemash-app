package org.bmsk.lifemash.notification.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun NotificationRouteScreen(
    onShowErrorSnackbar: (Throwable?) -> Unit,
    onBack: () -> Unit,
    viewModel: NotificationViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.markAllAsRead()
    }

    NotificationScreen(
        uiState = uiState,
        onRetry = viewModel::loadNotifications,
    )
}
