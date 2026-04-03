package org.bmsk.lifemash.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.bmsk.lifemash.auth.domain.model.AuthUser
import org.bmsk.lifemash.designsystem.component.AdaptiveNavigation
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

@Composable
internal fun MainTabScreen(
    currentUser: AuthUser?,
    onShowErrorSnackbar: (Throwable?) -> Unit,
    onNavigateToEventDetail: (String) -> Unit,
    onNavigateToProfileEdit: () -> Unit,
    onNavigateToEventCreate: (year: Int, month: Int, day: Int) -> Unit,
    onNavigateToUserProfile: (String) -> Unit,
    onNavigateToAuth: () -> Unit,
) {
    val tabNavController = rememberNavController()
    val tabs = listOf(ProfileTab, FeedTab, NotificationTab)

    val navBackStackEntry by tabNavController.currentBackStackEntryAsState()
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
        showNavigation = true, // 항상 true — squish 현상 해결의 핵심
        onItemClick = { tab ->
            if (tab.route != FEED_ROUTE && currentUser == null) {
                onNavigateToAuth()
                return@AdaptiveNavigation
            }
            val destination = tabRouteMap[tab.route] ?: return@AdaptiveNavigation
            tabNavController.navigate(destination) {
                popUpTo(tabNavController.graph.startDestinationId) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        },
        modifier = Modifier.fillMaxSize(),
    ) {
        NavHost(navController = tabNavController, startDestination = FeedRoute) {
            feedNavGraph(
                FeedNavGraphInfo(
                    onShowErrorSnackbar = onShowErrorSnackbar,
                    onNavigateToEventDetail = onNavigateToEventDetail,
                    onNavigateToUserProfile = onNavigateToUserProfile,
                )
            )
            notificationNavGraph(
                NotificationNavGraphInfo(
                    onShowErrorSnackbar = onShowErrorSnackbar,
                    onBack = { },
                    onNavigateToEventDetail = onNavigateToEventDetail,
                )
            )
            profileNavGraph(
                ProfileNavGraphInfo(
                    onShowErrorSnackbar = onShowErrorSnackbar,
                    onNavigateToEventDetail = onNavigateToEventDetail,
                    onNavigateToUserProfile = onNavigateToUserProfile,
                    onNavigateToProfileEdit = onNavigateToProfileEdit,
                    onNavigateToEventCreate = onNavigateToEventCreate,
                )
            )
        }
    }
}
