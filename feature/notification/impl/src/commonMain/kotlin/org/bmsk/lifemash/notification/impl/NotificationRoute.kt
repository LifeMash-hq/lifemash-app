package org.bmsk.lifemash.notification.impl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun NotificationRoute(
    onShowErrorSnackbar: (Throwable?) -> Unit,
    onBack: () -> Unit,
    onNavigateToEventDetail: (String) -> Unit = {},
    viewModel: NotificationViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.markAllAsRead()
    }

    NotificationScreen(
        uiState = uiState,
        onRetry = viewModel::loadNotifications,
        onNotificationClick = onNavigateToEventDetail,
    )
}
