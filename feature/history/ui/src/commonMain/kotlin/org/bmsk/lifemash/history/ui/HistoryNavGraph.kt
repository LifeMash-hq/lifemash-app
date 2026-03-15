package org.bmsk.lifemash.history.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.outlined.History
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.bmsk.lifemash.feature.designsystem.component.BottomNavItem
import org.bmsk.lifemash.history.api.HISTORY_ROUTE
import org.bmsk.lifemash.history.api.HistoryNavGraphInfo
import org.bmsk.lifemash.history.api.HistoryRoute

val HistoryTab = BottomNavItem(
    route = HISTORY_ROUTE,
    icon = Icons.Outlined.History,
    selectedIcon = Icons.Filled.History,
    label = "기록",
)

fun NavGraphBuilder.historyNavGraph(navInfo: HistoryNavGraphInfo) {
    composable<HistoryRoute> {
        HistoryRouteScreen(
            onClickArticle = navInfo.onClickArticle,
            onShowErrorSnackbar = navInfo.onShowErrorSnackbar,
        )
    }
}
