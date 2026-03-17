package org.bmsk.lifemash.notification.ui

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.bmsk.lifemash.notification.api.NotificationNavGraphInfo
import org.bmsk.lifemash.notification.api.NotificationRoute

fun NavGraphBuilder.notificationNavGraph(navInfo: NotificationNavGraphInfo) {
    composable<NotificationRoute> {
        NotificationRouteScreen(
            onShowErrorSnackbar = navInfo.onShowErrorSnackbar,
            onBack = navInfo.onBack,
        )
    }
}
