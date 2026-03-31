package org.bmsk.lifemash.profile.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun ProfileRouteScreen(
    onShowErrorSnackbar: (Throwable?) -> Unit,
    onNavigateToProfileEdit: () -> Unit = {},
    viewModel: ProfileViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadProfile("me")
    }

    MyProfileScreen(
        uiState = uiState,
        onEditClick = onNavigateToProfileEdit,
        onSubTabSelect = viewModel::selectSubTab,
        onCalendarDaySelect = viewModel::selectCalendarDay,
        onNavigateMonth = viewModel::navigateMonth,
    )
}
