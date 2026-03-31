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
import org.bmsk.lifemash.auth.api.AuthNavGraphInfo
import org.bmsk.lifemash.auth.api.AuthRoute
import org.bmsk.lifemash.auth.ui.authNavGraph
import org.bmsk.lifemash.calendar.api.CalendarNavGraphInfo
import org.bmsk.lifemash.calendar.ui.calendarNavGraph
import org.bmsk.lifemash.designsystem.component.AdaptiveNavigation
import org.bmsk.lifemash.eventdetail.api.EventDetailRoute
import org.bmsk.lifemash.feed.api.FEED_ROUTE
import org.bmsk.lifemash.feed.api.FeedNavGraphInfo
import org.bmsk.lifemash.feed.api.FeedRoute
import org.bmsk.lifemash.feed.ui.FeedTab
import org.bmsk.lifemash.feed.ui.feedNavGraph
import org.bmsk.lifemash.notification.api.NOTIFICATION_ROUTE
import org.bmsk.lifemash.notification.api.NotificationNavGraphInfo
import org.bmsk.lifemash.notification.api.NotificationRoute
import org.bmsk.lifemash.notification.ui.NotificationTab
import org.bmsk.lifemash.notification.ui.notificationNavGraph
import org.bmsk.lifemash.profile.api.PROFILE_ROUTE
import org.bmsk.lifemash.profile.api.ProfileNavGraphInfo
import org.bmsk.lifemash.profile.api.ProfileRoute
import org.bmsk.lifemash.profile.ui.ProfileTab
import org.bmsk.lifemash.profile.ui.profileNavGraph
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    onShowErrorSnackbar: (Throwable?) -> Unit = {}
) {
    val navController = rememberNavController()
    val tabs = listOf(ProfileTab, FeedTab, NotificationTab)

    val mainViewModel = koinViewModel<MainViewModel>()
    val authState by mainViewModel.authState.collectAsState()

    if (authState is AuthState.Loading) return

    val currentUser = when (val state = authState) {
        is AuthState.Authenticated -> state.user
        is AuthState.Unauthenticated, AuthState.Loading -> null
    }

    val startDestination = if (currentUser != null) FeedRoute else AuthRoute

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentTabRoute = navBackStackEntry?.destination?.let { dest ->
        when {
            dest.hasRoute<FeedRoute>() -> FEED_ROUTE
            dest.hasRoute<NotificationRoute>() -> NOTIFICATION_ROUTE
            dest.hasRoute<ProfileRoute>() -> PROFILE_ROUTE
            else -> null
        }
    }

    val tabRouteMap: Map<String, Any> = mapOf(
        FEED_ROUTE to FeedRoute,
        NOTIFICATION_ROUTE to NotificationRoute,
        PROFILE_ROUTE to ProfileRoute,
    )

    AdaptiveNavigation(
        tabs = tabs,
        currentRoute = currentTabRoute,
        showNavigation = currentTabRoute != null,
        onItemClick = { tab ->
            if (tab.route != FEED_ROUTE && currentUser == null) {
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
        modifier = modifier.fillMaxSize(),
    ) {
        NavHost(navController = navController, startDestination = startDestination) {
            feedNavGraph(
                FeedNavGraphInfo(
                    onShowErrorSnackbar = onShowErrorSnackbar,
                    onNavigateToEventDetail = { eventId ->
                        navController.navigate(EventDetailRoute(eventId))
                    },
                    onNavigateToUserProfile = { /* TODO */ },
                )
            )
            notificationNavGraph(
                NotificationNavGraphInfo(
                    onShowErrorSnackbar = onShowErrorSnackbar,
                    onBack = { navController.popBackStack() },
                )
            )
            profileNavGraph(
                navInfo = ProfileNavGraphInfo(
                    onShowErrorSnackbar = onShowErrorSnackbar,
                    onNavigateToEventDetail = { eventId ->
                        navController.navigate(EventDetailRoute(eventId))
                    },
                    onNavigateToUserProfile = { /* TODO */ },
                ),
                navController = navController,
            )
            calendarNavGraph(
                CalendarNavGraphInfo(
                    onShowErrorSnackbar = onShowErrorSnackbar,
                )
            )
            authNavGraph(
                AuthNavGraphInfo(
                    onSignInComplete = {
                        navController.popBackStack()
                        navController.navigate(FeedRoute) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                        }
                    },
                    onShowErrorSnackbar = onShowErrorSnackbar,
                )
            )
        }
    }
}
