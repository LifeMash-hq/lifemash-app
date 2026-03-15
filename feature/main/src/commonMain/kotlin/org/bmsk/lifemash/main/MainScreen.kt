package org.bmsk.lifemash.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.bmsk.lifemash.feature.designsystem.component.AdaptiveNavigation
import org.bmsk.lifemash.feed.api.FEED_ROUTE
import org.bmsk.lifemash.feed.api.FeedNavGraphInfo
import org.bmsk.lifemash.feed.api.FeedRoute
import org.bmsk.lifemash.feed.ui.FeedTab
import org.bmsk.lifemash.feed.ui.feedNavGraph
import org.bmsk.lifemash.history.api.HISTORY_ROUTE
import org.bmsk.lifemash.history.api.HistoryNavGraphInfo
import org.bmsk.lifemash.history.api.HistoryRoute
import org.bmsk.lifemash.history.ui.HistoryTab
import org.bmsk.lifemash.history.ui.historyNavGraph
import org.bmsk.lifemash.scrap.api.SCRAP_ROUTE
import org.bmsk.lifemash.scrap.api.ScrapNavGraphInfo
import org.bmsk.lifemash.scrap.api.ScrapRoute
import org.bmsk.lifemash.scrap.ui.ScrapTab
import org.bmsk.lifemash.scrap.ui.scrapNavGraph
import org.bmsk.lifemash.feature.shared.webview.WebViewNavGraphInfo
import org.bmsk.lifemash.feature.shared.webview.WebViewRoute
import org.bmsk.lifemash.feature.shared.webview.webViewNavGraph

@Composable
fun MainScreen(
    onShowErrorSnackbar: (Throwable?) -> Unit = {},
) {
    val navController = rememberNavController()
    val tabs = listOf(FeedTab, ScrapTab, HistoryTab)

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val destinationRoute = navBackStackEntry?.destination?.route
    val currentTabRoute = when {
        destinationRoute?.contains("FeedRoute") == true -> FEED_ROUTE
        destinationRoute?.contains("ScrapRoute") == true -> SCRAP_ROUTE
        destinationRoute?.contains("HistoryRoute") == true -> HISTORY_ROUTE
        else -> null
    }

    val tabRouteMap = mapOf(
        FEED_ROUTE to FeedRoute,
        SCRAP_ROUTE to ScrapRoute,
        HISTORY_ROUTE to HistoryRoute,
    )

    val navigateWebView: (String) -> Unit = { url ->
        navController.navigate(WebViewRoute(url))
    }

    AdaptiveNavigation(
        tabs = tabs,
        currentRoute = currentTabRoute,
        onItemClick = { tab ->
            val destination = tabRouteMap[tab.route] ?: return@AdaptiveNavigation
            navController.navigate(destination) {
                popUpTo(navController.graph.startDestinationId) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        },
    ) {
        NavHost(navController = navController, startDestination = FeedRoute) {
            feedNavGraph(FeedNavGraphInfo(onArticleOpen = navigateWebView))
            scrapNavGraph(ScrapNavGraphInfo(onClickNews = navigateWebView, onShowErrorSnackbar = onShowErrorSnackbar))
            historyNavGraph(HistoryNavGraphInfo(onClickArticle = navigateWebView, onShowErrorSnackbar = onShowErrorSnackbar))
            webViewNavGraph(WebViewNavGraphInfo(onShowErrorSnackbar))
        }
    }
}
