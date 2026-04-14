package org.bmsk.lifemash.profile.impl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun UserProfileRouteScreen(
    userId: String,
    onBack: () -> Unit,
    onNavigateToEventDetail: (String) -> Unit = {},
    viewModel: ProfileViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showFollowSheet by remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        viewModel.loadProfile(userId)
    }

    UserProfileScreen(
        uiState = uiState,
        onBackClick = onBack,
        onFollowToggle = {
            (uiState as? ProfileUiState.Loaded)?.let { loaded ->
                viewModel.toggleFollow(loaded, userId)
            }
        },
        onFollowerClick = { showFollowSheet = true },
        onFollowingClick = { showFollowSheet = true },
        onSubTabSelect = viewModel::selectSubTab,
        onCalendarDaySelect = viewModel::selectCalendarDay,
        onNavigateMonth = viewModel::navigateMonth,
        onEventClick = onNavigateToEventDetail,
    )

    if (showFollowSheet) {
        FollowListSheet(
            profileUserId = userId,
            onDismiss = { showFollowSheet = false },
        )
    }
}
