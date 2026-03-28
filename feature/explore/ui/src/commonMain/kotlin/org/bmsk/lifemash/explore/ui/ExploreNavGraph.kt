package org.bmsk.lifemash.explore.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Search
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.bmsk.lifemash.feature.designsystem.component.BottomNavItem
import org.bmsk.lifemash.explore.api.EXPLORE_ROUTE
import org.bmsk.lifemash.explore.api.ExploreNavGraphInfo
import org.bmsk.lifemash.explore.api.ExploreRoute

val ExploreTab = BottomNavItem(
    route = EXPLORE_ROUTE,
    icon = Icons.Outlined.Search,
    selectedIcon = Icons.Filled.Search,
    label = "탐색",
)

fun NavGraphBuilder.exploreNavGraph(navInfo: ExploreNavGraphInfo) {
    composable<ExploreRoute> {
        ExploreRouteScreen(onShowErrorSnackbar = navInfo.onShowErrorSnackbar)
    }
}
