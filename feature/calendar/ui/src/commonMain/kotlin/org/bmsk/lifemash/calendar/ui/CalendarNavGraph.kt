package org.bmsk.lifemash.calendar.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.bmsk.lifemash.calendar.api.CALENDAR_ROUTE
import org.bmsk.lifemash.calendar.api.CalendarNavGraphInfo
import org.bmsk.lifemash.calendar.api.CalendarRoute
import org.bmsk.lifemash.feature.designsystem.component.BottomNavItem

val CalendarTab = BottomNavItem(
    route = CALENDAR_ROUTE,
    icon = Icons.Outlined.CalendarMonth,
    selectedIcon = Icons.Filled.CalendarMonth,
    label = "캘린더",
)

fun NavGraphBuilder.calendarNavGraph(navInfo: CalendarNavGraphInfo) {
    composable<CalendarRoute> {
        CalendarRouteScreen(
            onShowErrorSnackbar = navInfo.onShowErrorSnackbar,
        )
    }
}
