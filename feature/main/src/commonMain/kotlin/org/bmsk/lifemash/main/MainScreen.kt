package org.bmsk.lifemash.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.bmsk.lifemash.assistant.api.ASSISTANT_ROUTE
import org.bmsk.lifemash.assistant.api.AssistantNavGraphInfo
import org.bmsk.lifemash.assistant.api.AssistantRoute
import org.bmsk.lifemash.assistant.ui.AssistantTab
import org.bmsk.lifemash.assistant.ui.assistantNavGraph
import org.bmsk.lifemash.auth.api.AuthNavGraphInfo
import org.bmsk.lifemash.auth.api.AuthRoute
import org.bmsk.lifemash.auth.ui.authNavGraph
import org.bmsk.lifemash.calendar.api.CALENDAR_ROUTE
import org.bmsk.lifemash.calendar.api.CalendarNavGraphInfo
import org.bmsk.lifemash.calendar.api.CalendarRoute
import org.bmsk.lifemash.calendar.ui.CalendarTab
import org.bmsk.lifemash.calendar.ui.calendarNavGraph
import org.bmsk.lifemash.feature.designsystem.component.AdaptiveNavigation
import org.bmsk.lifemash.home.api.BlockSettingsRoute
import org.bmsk.lifemash.home.api.HOME_ROUTE
import org.bmsk.lifemash.home.api.HomeRoute
import org.bmsk.lifemash.home.ui.HomeTab
import org.bmsk.lifemash.home.ui.homeNavGraph
import org.bmsk.lifemash.notification.api.NotificationNavGraphInfo
import org.bmsk.lifemash.notification.api.NotificationRoute
import org.bmsk.lifemash.notification.ui.notificationNavGraph
import org.bmsk.lifemash.feature.shared.webview.WebViewNavGraphInfo
import org.bmsk.lifemash.feature.shared.webview.WebViewRoute
import org.bmsk.lifemash.feature.shared.webview.webViewNavGraph
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MainScreen(
    onShowErrorSnackbar: (Throwable?) -> Unit = {},
) {
    val navController = rememberNavController()
    val tabs = listOf(HomeTab, CalendarTab, AssistantTab)

    val mainViewModel = koinViewModel<MainViewModel>()
    val currentUser by mainViewModel.currentUser.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentTabRoute = navBackStackEntry?.destination?.let { dest ->
        when {
            dest.hasRoute<HomeRoute>() -> HOME_ROUTE
            dest.hasRoute<BlockSettingsRoute>() -> HOME_ROUTE
            dest.hasRoute<CalendarRoute>() -> CALENDAR_ROUTE
            dest.hasRoute<AssistantRoute>() -> ASSISTANT_ROUTE
            else -> null
        }
    }

    val tabRouteMap = mapOf(
        HOME_ROUTE to HomeRoute,
        CALENDAR_ROUTE to CalendarRoute,
        ASSISTANT_ROUTE to AssistantRoute,
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
            if (tab.route in setOf(CALENDAR_ROUTE, ASSISTANT_ROUTE) && currentUser == null) {
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
        modifier = Modifier.fillMaxSize(),
    ) {
        NavHost(navController = navController, startDestination = HomeRoute) {
            homeNavGraph(
                navController = navController,
                onNavigateToAssistant = {
                    if (currentUser == null) {
                        navController.navigate(AuthRoute)
                    } else {
                        navController.navigate(AssistantRoute) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                        }
                    }
                },
            )
            webViewNavGraph(WebViewNavGraphInfo(onShowErrorSnackbar))
            notificationNavGraph(NotificationNavGraphInfo(
                onShowErrorSnackbar = onShowErrorSnackbar,
                onBack = { navController.popBackStack() },
            ))
            calendarNavGraph(CalendarNavGraphInfo(
                onShowErrorSnackbar = onShowErrorSnackbar,
            ))
            assistantNavGraph(AssistantNavGraphInfo(
                onShowErrorSnackbar = onShowErrorSnackbar,
                onBack = { navController.popBackStack() },
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
