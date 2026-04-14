package org.bmsk.lifemash.profile.impl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun ProfileRouteScreen(
    onShowErrorSnackbar: (Throwable?) -> Unit,
    onNavigateToProfileEdit: () -> Unit = {},
    onNavigateToEventCreate: (year: Int, month: Int, day: Int) -> Unit = { _, _, _ -> },
    onNavigateToEventDetail: (String) -> Unit = {},
    onNavigateToUserProfile: (String) -> Unit = {},
    navController: NavController? = null,
    viewModel: ProfileViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadProfile("me")
    }

    LaunchedEffect(uiState.errorMessage) {
        val msg = uiState.errorMessage ?: return@LaunchedEffect
        onShowErrorSnackbar(Exception(msg))
        viewModel.clearError()
    }

    LaunchedEffect(navController) {
        val savedStateHandle = navController?.currentBackStackEntry?.savedStateHandle ?: return@LaunchedEffect
        savedStateHandle.getStateFlow("event_created", false).collect { created ->
            if (created) {
                viewModel.reloadEvents()
                savedStateHandle.remove<Boolean>("event_created")
            }
        }
    }

    MyProfileScreen(
        uiState = uiState,
        onEditClick = onNavigateToProfileEdit,
        onFollowerClick = viewModel::showFollowSheet,
        onFollowingClick = viewModel::showFollowSheet,
        onSubTabSelect = viewModel::selectSubTab,
        onCalendarDaySelect = viewModel::selectCalendarDay,
        onNavigateMonth = viewModel::navigateMonth,
        onNavigateToEventCreate = onNavigateToEventCreate,
        onEventClick = onNavigateToEventDetail,
    )

    if (uiState.isFollowSheetVisible && uiState.profile != null) {
        FollowListSheet(
            profileUserId = uiState.profile!!.id,
            onDismiss = viewModel::dismissFollowSheet,
            onUserClick = { userId ->
                viewModel.dismissFollowSheet()
                onNavigateToUserProfile(userId)
            },
        )
    }
}
