package org.bmsk.lifemash.profile.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.bmsk.lifemash.feature.designsystem.component.BottomNavItem
import org.bmsk.lifemash.profile.api.PROFILE_ROUTE
import org.bmsk.lifemash.profile.api.ProfileNavGraphInfo
import org.bmsk.lifemash.profile.api.ProfileRoute

val ProfileTab = BottomNavItem(
    route = PROFILE_ROUTE,
    icon = Icons.Outlined.CalendarMonth,
    selectedIcon = Icons.Filled.CalendarMonth,
    label = "나",
)

fun NavGraphBuilder.profileNavGraph(navInfo: ProfileNavGraphInfo) {
    composable<ProfileRoute> {
        ProfileRouteScreen(onShowErrorSnackbar = navInfo.onShowErrorSnackbar)
    }
}
