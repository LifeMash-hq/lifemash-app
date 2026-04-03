package org.bmsk.lifemash.profile.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    var showFollowSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadProfile("me")
    }

    LaunchedEffect(uiState) {
        val state = uiState
        if (state is ProfileUiState.Loaded && state.errorMessage != null) {
            onShowErrorSnackbar(Exception(state.errorMessage))
            viewModel.clearError()
        }
    }

    // 일정 생성 완료 결과 수신
    LaunchedEffect(navController) {
        val savedStateHandle = navController?.currentBackStackEntry?.savedStateHandle ?: return@LaunchedEffect
        savedStateHandle.getStateFlow("event_created", false).collect { created ->
            if (created) {
                viewModel.reloadEvents()
                savedStateHandle.remove<Boolean>("event_created")
            }
        }
    }

    val profileUserId = (uiState as? ProfileUiState.Loaded)?.profile?.id

    MyProfileScreen(
        uiState = uiState,
        onEditClick = onNavigateToProfileEdit,
        onFollowerClick = { showFollowSheet = true },
        onFollowingClick = { showFollowSheet = true },
        onSubTabSelect = viewModel::selectSubTab,
        onCalendarDaySelect = viewModel::selectCalendarDay,
        onNavigateMonth = viewModel::navigateMonth,
        onNavigateToEventCreate = { year, month, day ->
            onNavigateToEventCreate(year, month, day)
        },
        onEventClick = onNavigateToEventDetail,
    )

    if (showFollowSheet && profileUserId != null) {
        FollowListSheet(
            profileUserId = profileUserId,
            onDismiss = { showFollowSheet = false },
            onUserClick = { userId ->
                showFollowSheet = false
                onNavigateToUserProfile(userId)
            },
        )
    }
}
