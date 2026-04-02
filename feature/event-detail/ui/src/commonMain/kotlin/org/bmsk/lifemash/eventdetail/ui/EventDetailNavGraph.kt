package org.bmsk.lifemash.eventdetail.ui

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import org.bmsk.lifemash.eventdetail.api.EventDetailRoute

fun NavGraphBuilder.eventDetailNavGraph(onBack: () -> Unit) {
    composable<EventDetailRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<EventDetailRoute>()
        EventDetailRouteScreen(
            eventId = route.eventId,
            onBack = onBack,
        )
    }
}
