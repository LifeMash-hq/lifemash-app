package org.bmsk.lifemash.feed.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.outlined.People
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.bmsk.lifemash.designsystem.component.BottomNavItem
import org.bmsk.lifemash.feed.api.FEED_ROUTE
import org.bmsk.lifemash.feed.api.FeedNavGraphInfo
import org.bmsk.lifemash.feed.api.FeedRoute

val FeedTab = BottomNavItem(
    route = FEED_ROUTE,
    icon = Icons.Outlined.People,
    selectedIcon = Icons.Filled.People,
    label = "피드",
)

fun NavGraphBuilder.feedNavGraph(navInfo: FeedNavGraphInfo) {
    composable<FeedRoute> {
        FeedRouteScreen(onShowErrorSnackbar = navInfo.onShowErrorSnackbar)
    }
}
