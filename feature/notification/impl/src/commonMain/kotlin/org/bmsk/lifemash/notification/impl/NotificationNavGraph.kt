package org.bmsk.lifemash.notification.impl

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Notifications
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.bmsk.lifemash.designsystem.component.BottomNavItem
import org.bmsk.lifemash.notification.api.NOTIFICATION_ROUTE
import org.bmsk.lifemash.notification.api.NotificationNavGraphInfo
import org.bmsk.lifemash.notification.api.NotificationRoute

val NotificationTab = BottomNavItem(
    route = NOTIFICATION_ROUTE,
    icon = Icons.Outlined.Notifications,
    selectedIcon = Icons.Filled.Notifications,
    label = "알림",
)

fun NavGraphBuilder.notificationNavGraph(navInfo: NotificationNavGraphInfo) {
    composable<NotificationRoute> {
        NotificationRoute(
            onShowErrorSnackbar = navInfo.onShowErrorSnackbar,
            onBack = navInfo.onBack,
            onNavigateToEventDetail = navInfo.onNavigateToEventDetail,
        )
    }
}
