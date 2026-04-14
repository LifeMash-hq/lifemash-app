package org.bmsk.lifemash.profile.impl

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import org.bmsk.lifemash.designsystem.component.BottomNavItem
import org.bmsk.lifemash.profile.api.PROFILE_ROUTE
import org.bmsk.lifemash.profile.api.ProfileEditRoute
import org.bmsk.lifemash.profile.api.ProfileNavGraphInfo
import org.bmsk.lifemash.profile.api.ProfileRoute
import org.bmsk.lifemash.profile.api.UserProfileRoute

val ProfileTab = BottomNavItem(
    route = PROFILE_ROUTE,
    icon = Icons.Outlined.CalendarMonth,
    selectedIcon = Icons.Filled.CalendarMonth,
    label = "나",
)

fun NavGraphBuilder.profileNavGraph(navInfo: ProfileNavGraphInfo) {
    composable<ProfileRoute> {
        ProfileRouteScreen(
            onShowErrorSnackbar = navInfo.onShowErrorSnackbar,
            onNavigateToProfileEdit = navInfo.onNavigateToProfileEdit,
            onNavigateToEventCreate = navInfo.onNavigateToEventCreate,
            onNavigateToEventDetail = navInfo.onNavigateToEventDetail,
            onNavigateToUserProfile = navInfo.onNavigateToUserProfile,
        )
    }
}

// ProfileEditRoute는 Root NavHost에 등록되어야 하므로 별도 함수로 노출
fun NavGraphBuilder.profileEditNavGraph(onBack: () -> Unit) {
    composable<ProfileEditRoute> {
        ProfileEditRouteScreen(onBack = onBack)
    }
}

// UserProfileRoute는 Root NavHost에 등록되어야 하므로 별도 함수로 노출
fun NavGraphBuilder.userProfileNavGraph(
    onBack: () -> Unit,
    onNavigateToEventDetail: (String) -> Unit = {},
) {
    composable<UserProfileRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<UserProfileRoute>()
        UserProfileRouteScreen(
            userId = route.userId,
            onBack = onBack,
            onNavigateToEventDetail = onNavigateToEventDetail,
        )
    }
}
