package org.bmsk.lifemash.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.bmsk.lifemash.auth.api.AuthNavGraphInfo
import org.bmsk.lifemash.auth.api.AuthRoute
import org.bmsk.lifemash.auth.domain.usecase.GetCurrentUserUseCase
import org.bmsk.lifemash.auth.ui.authNavGraph
import org.bmsk.lifemash.calendar.api.CALENDAR_ROUTE
import org.bmsk.lifemash.calendar.api.CalendarNavGraphInfo
import org.bmsk.lifemash.calendar.api.CalendarRoute
import org.bmsk.lifemash.calendar.ui.CalendarTab
import org.bmsk.lifemash.calendar.ui.calendarNavGraph
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
import org.bmsk.lifemash.notification.api.NotificationNavGraphInfo
import org.bmsk.lifemash.notification.api.NotificationRoute
import org.bmsk.lifemash.notification.ui.notificationNavGraph
import org.bmsk.lifemash.scrap.api.SCRAP_ROUTE
import org.bmsk.lifemash.scrap.api.ScrapNavGraphInfo
import org.bmsk.lifemash.scrap.api.ScrapRoute
import org.bmsk.lifemash.scrap.ui.ScrapTab
import org.bmsk.lifemash.scrap.ui.scrapNavGraph
import org.bmsk.lifemash.feature.shared.webview.WebViewNavGraphInfo
import org.bmsk.lifemash.feature.shared.webview.WebViewRoute
import org.bmsk.lifemash.feature.shared.webview.webViewNavGraph
import org.koin.compose.koinInject

@Composable
fun MainScreen(
    onShowErrorSnackbar: (Throwable?) -> Unit = {},
) {
    val navController = rememberNavController()
    val tabs = listOf(FeedTab, ScrapTab, HistoryTab, CalendarTab)

    val getCurrentUserUseCase = koinInject<GetCurrentUserUseCase>()
    val currentUser by getCurrentUserUseCase().collectAsState(initial = null)

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val destinationRoute = navBackStackEntry?.destination?.route
    val currentTabRoute = when {
        destinationRoute?.contains("FeedRoute") == true -> FEED_ROUTE
        destinationRoute?.contains("ScrapRoute") == true -> SCRAP_ROUTE
        destinationRoute?.contains("HistoryRoute") == true -> HISTORY_ROUTE
        destinationRoute?.contains("CalendarRoute") == true -> CALENDAR_ROUTE
        else -> null
    }

    val tabRouteMap = mapOf(
        FEED_ROUTE to FeedRoute,
        SCRAP_ROUTE to ScrapRoute,
        HISTORY_ROUTE to HistoryRoute,
        CALENDAR_ROUTE to CalendarRoute,
    )

    val navigateWebView: (String) -> Unit = { url ->
        navController.navigate(WebViewRoute(url))
    }

    val navigateNotification: () -> Unit = {
        navController.navigate(NotificationRoute)
    }

    AdaptiveNavigation(
        tabs = tabs,
        currentRoute = currentTabRoute,
        onItemClick = { tab ->
            // 캘린더 탭: 로그인 안 됐으면 로그인 화면으로
            if (tab.route == CALENDAR_ROUTE && currentUser == null) {
                navController.navigate(AuthRoute)
                return@AdaptiveNavigation
            }
            val destination = tabRouteMap[tab.route] ?: return@AdaptiveNavigation
            navController.navigate(destination) {
                popUpTo(navController.graph.startDestinationId) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        },
    ) {
        NavHost(navController = navController, startDestination = FeedRoute) {
            feedNavGraph(FeedNavGraphInfo(
                onArticleOpen = navigateWebView,
                onNotificationClick = navigateNotification,
            ))
            scrapNavGraph(ScrapNavGraphInfo(onClickNews = navigateWebView, onShowErrorSnackbar = onShowErrorSnackbar))
            historyNavGraph(HistoryNavGraphInfo(onClickArticle = navigateWebView, onShowErrorSnackbar = onShowErrorSnackbar))
            webViewNavGraph(WebViewNavGraphInfo(onShowErrorSnackbar))
            notificationNavGraph(NotificationNavGraphInfo(
                onShowErrorSnackbar = onShowErrorSnackbar,
                onBack = { navController.popBackStack() },
            ))
            calendarNavGraph(CalendarNavGraphInfo(
                onShowErrorSnackbar = onShowErrorSnackbar,
            ))
            authNavGraph(AuthNavGraphInfo(
                onSignInComplete = {
                    navController.popBackStack()
                    navController.navigate(CalendarRoute) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                    }
                },
                onShowErrorSnackbar = onShowErrorSnackbar,
            ))
        }
    }
}
