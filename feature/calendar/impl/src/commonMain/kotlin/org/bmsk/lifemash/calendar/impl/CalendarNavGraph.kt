package org.bmsk.lifemash.calendar.impl

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import org.bmsk.lifemash.calendar.api.CalendarNavGraphInfo
import org.bmsk.lifemash.calendar.api.CalendarRoute
import org.bmsk.lifemash.calendar.api.EventCreateRoute
import org.bmsk.lifemash.calendar.api.EventEditRoute
import org.bmsk.lifemash.domain.calendar.Event
import org.bmsk.lifemash.domain.calendar.EventTiming
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
                val (startMs, endMs, isAllDay) = when (val t = event.timing) {
                    is EventTiming.AllDay ->
                        Triple(t.date.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds(), null, true)
                    is EventTiming.Timed ->
                        Triple(t.start.toEpochMilliseconds(), t.end.toEpochMilliseconds(), false)
                }
                navController.navigate(
                    EventEditRoute(
                        groupId = groupId,
                        eventId = event.id,
                        eventTitle = event.title,
                        eventDescription = event.description,
                        eventColor = event.color,
                        eventIsAllDay = isAllDay,
                        eventStartAt = startMs,
                        eventEndAt = endMs,
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
        val startAt = Instant.fromEpochMilliseconds(route.eventStartAt)
        val endAt = route.eventEndAt?.let { Instant.fromEpochMilliseconds(it) }

        val timing: EventTiming = if (route.eventIsAllDay) {
            EventTiming.AllDay(startAt.toLocalDateTime(TimeZone.UTC).date)
        } else {
            EventTiming.Timed(
                start = startAt,
                end = requireNotNull(endAt) { "Timed event must have endAt (id=${route.eventId})" },
            )
        }

        val existingEvent = Event(
            id = route.eventId,
            groupId = route.groupId,
            authorId = route.eventAuthorId,
            title = route.eventTitle,
            description = route.eventDescription,
            location = route.eventLocation,
            timing = timing,
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
