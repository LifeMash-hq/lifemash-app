package org.bmsk.lifemash.calendar.impl

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.bmsk.lifemash.calendar.api.CalendarNavGraphInfo
import org.bmsk.lifemash.calendar.api.CalendarRoute
import org.bmsk.lifemash.calendar.api.EventCreateRoute
import org.bmsk.lifemash.calendar.api.EventEditRoute
import org.bmsk.lifemash.domain.calendar.Event
import kotlin.time.Instant

fun NavGraphBuilder.calendarNavGraph(navInfo: CalendarNavGraphInfo, navController: NavController) {
    composable<CalendarRoute> {
        CalendarRoute(
            onShowErrorSnackbar = navInfo.onShowErrorSnackbar,
            onBack = navInfo.onBack,
            onNavigateToEventCreate = { year, month, day, groupId ->
                navController.navigate(EventCreateRoute(year, month, day, groupId))
            },
            onNavigateToEventEdit = { groupId, event ->
                val tz = TimeZone.currentSystemDefault()
                val startLocal = event.startAt.toLocalDateTime(tz)
                navController.navigate(
                    EventEditRoute(
                        groupId = groupId,
                        eventId = event.id,
                        eventTitle = event.title,
                        eventDescription = event.description,
                        eventColor = event.color,
                        eventIsAllDay = event.isAllDay,
                        eventStartAt = event.startAt.toEpochMilliseconds(),
                        eventEndAt = event.endAt?.toEpochMilliseconds(),
                        eventLocation = event.location,
                        eventAuthorId = event.authorId,
                        eventVisibility = event.visibility.toRouteString(),
                        eventCreatedAt = event.createdAt.toEpochMilliseconds(),
                        eventUpdatedAt = event.updatedAt.toEpochMilliseconds(),
                    )
                )
            },
            navController = navController,
        )
    }

    composable<EventCreateRoute> { entry ->
        val route = entry.toRoute<EventCreateRoute>()
        EventCreateRoute(
            year = route.year,
            month = route.month,
            day = route.day,
            groupId = route.groupId,
            onBack = { navController.popBackStack() },
            onEventCreated = {
                navController.previousBackStackEntry
                    ?.savedStateHandle?.set("event_changed", true)
                navController.popBackStack()
            },
        )
    }

    composable<EventEditRoute> { entry ->
        val route = entry.toRoute<EventEditRoute>()
        val tz = TimeZone.currentSystemDefault()
        val startAt = Instant.fromEpochMilliseconds(route.eventStartAt)
        val endAt = route.eventEndAt?.let { Instant.fromEpochMilliseconds(it) }
        val startLocal = startAt.toLocalDateTime(tz)
        val endLocal = endAt?.toLocalDateTime(tz)

        val existingEvent = Event(
            id = route.eventId,
            groupId = route.groupId,
            authorId = route.eventAuthorId,
            title = route.eventTitle,
            description = route.eventDescription,
            location = route.eventLocation,
            startAt = startAt,
            endAt = endAt,
            isAllDay = route.eventIsAllDay,
            color = route.eventColor,
            visibility = parseEventVisibility(route.eventVisibility),
            createdAt = Instant.fromEpochMilliseconds(route.eventCreatedAt),
            updatedAt = Instant.fromEpochMilliseconds(route.eventUpdatedAt),
        )

        EventEditRoute(
            groupId = route.groupId,
            existingEvent = existingEvent,
            onBack = { navController.popBackStack() },
            onEventUpdated = {
                navController.previousBackStackEntry
                    ?.savedStateHandle?.set("event_changed", true)
                navController.popBackStack()
            },
        )
    }
}
