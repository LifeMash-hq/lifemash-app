package org.bmsk.lifemash.home.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Home
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.bmsk.lifemash.feature.designsystem.component.BottomNavItem
import org.bmsk.lifemash.home.api.BlockSettingsRoute
import org.bmsk.lifemash.home.api.HOME_ROUTE
import org.bmsk.lifemash.home.api.HomeRoute
import org.bmsk.lifemash.home.api.MarketplaceRoute

val HomeTab = BottomNavItem(
    route = HOME_ROUTE,
    icon = Icons.Outlined.Home,
    selectedIcon = Icons.Filled.Home,
    label = "홈",
)

fun NavGraphBuilder.homeNavGraph(
    navController: NavController,
    onNavigateToAssistant: () -> Unit,
) {
    composable<HomeRoute> {
        HomeRouteScreen(
            onNavigateToBlockSettings = { navController.navigate(BlockSettingsRoute) },
            onNavigateToAssistant = onNavigateToAssistant,
        )
    }
    composable<BlockSettingsRoute> {
        BlockSettingsRouteScreen(
            onBack = { navController.popBackStack() },
            onNavigateToMarketplace = { navController.navigate(MarketplaceRoute) },
        )
    }
    composable<MarketplaceRoute> {
        MarketplaceRouteScreen(onBack = { navController.popBackStack() })
    }
}
