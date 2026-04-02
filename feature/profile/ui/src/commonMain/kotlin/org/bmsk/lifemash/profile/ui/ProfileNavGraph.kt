package org.bmsk.lifemash.profile.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.bmsk.lifemash.calendar.api.EventCreateRoute
import org.bmsk.lifemash.designsystem.component.BottomNavItem
import org.bmsk.lifemash.profile.api.PROFILE_ROUTE
import org.bmsk.lifemash.profile.api.ProfileEditRoute
import org.bmsk.lifemash.profile.api.ProfileNavGraphInfo
import org.bmsk.lifemash.profile.api.ProfileRoute

val ProfileTab = BottomNavItem(
    route = PROFILE_ROUTE,
    icon = Icons.Outlined.CalendarMonth,
    selectedIcon = Icons.Filled.CalendarMonth,
    label = "나",
)

fun NavGraphBuilder.profileNavGraph(navInfo: ProfileNavGraphInfo, navController: NavController) {
    composable<ProfileRoute> {
        ProfileRouteScreen(
            onShowErrorSnackbar = navInfo.onShowErrorSnackbar,
            onNavigateToProfileEdit = { navController.navigate(ProfileEditRoute) },
            onNavigateToEventCreate = { year, month, day ->
                navController.navigate(EventCreateRoute(year, month, day))
            },
            onNavigateToEventDetail = navInfo.onNavigateToEventDetail,
            navController = navController,
        )
    }
    composable<ProfileEditRoute> {
        ProfileEditRouteScreen(onBack = { navController.popBackStack() })
    }
}
