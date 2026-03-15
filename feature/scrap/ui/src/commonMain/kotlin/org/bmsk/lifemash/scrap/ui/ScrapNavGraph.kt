package org.bmsk.lifemash.scrap.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.bmsk.lifemash.feature.designsystem.component.BottomNavItem
import org.bmsk.lifemash.scrap.api.SCRAP_ROUTE
import org.bmsk.lifemash.scrap.api.ScrapNavGraphInfo
import org.bmsk.lifemash.scrap.api.ScrapRoute

val ScrapTab = BottomNavItem(
    route = SCRAP_ROUTE,
    icon = Icons.Outlined.Bookmarks,
    selectedIcon = Icons.Filled.Bookmarks,
    label = "스크랩",
)

fun NavGraphBuilder.scrapNavGraph(navInfo: ScrapNavGraphInfo) {
    composable<ScrapRoute> {
        ScrapRouteScreen(
            onClickNews = navInfo.onClickNews,
            onShowErrorSnackbar = navInfo.onShowErrorSnackbar,
        )
    }
}
