package org.bmsk.lifemash.domain.usecase.moment

import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import org.bmsk.lifemash.domain.calendar.Event
import org.bmsk.lifemash.domain.calendar.EventRepository
import org.bmsk.lifemash.domain.calendar.GroupRepository

class GetCurrentMonthGroupEventsUseCase(
    private val groupRepository: GroupRepository,
    private val eventRepository: EventRepository,
) {
    @OptIn(ExperimentalTime::class)
    suspend operator fun invoke(): List<Event> {
        val groups = groupRepository.getMyGroups()
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        return groups.flatMap { group ->
            eventRepository.getMonthEvents(group.id, now.year, now.month.number)
        }.sortedBy { it.startAt }
    }
}
