package org.bmsk.lifemash.profile.impl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun UserProfileRoute(
    userId: String,
    onBack: () -> Unit,
    onNavigateToEventDetail: (String) -> Unit = {},
    viewModel: ProfileViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(userId) {
        viewModel.loadProfile(userId)
    }

    UserProfileScreen(
        uiState = uiState,
        onBackClick = onBack,
        onFollowToggle = { viewModel.toggleFollow(userId) },
        onFollowerClick = viewModel::showFollowSheet,
        onFollowingClick = viewModel::showFollowSheet,
        onSubTabSelect = viewModel::selectSubTab,
        onCalendarDaySelect = viewModel::selectCalendarDay,
        onNavigateMonth = viewModel::navigateMonth,
        onEventClick = onNavigateToEventDetail,
    )

    if (uiState.isFollowSheetVisible) {
        FollowListSheet(
            profileUserId = userId,
            onDismiss = viewModel::dismissFollowSheet,
        )
    }
}
