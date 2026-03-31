package org.bmsk.lifemash.blocks

import kotlin.time.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.bmsk.lifemash.model.blocks.BlockGroupDto
import org.bmsk.lifemash.model.blocks.BlocksTodayResponse
import org.bmsk.lifemash.model.blocks.TodayEventDto
import org.bmsk.lifemash.event.EventRepository
import org.bmsk.lifemash.group.GroupRepository
import kotlin.uuid.Uuid

class BlocksServiceImpl(
    private val eventRepository: EventRepository,
    private val groupRepository: GroupRepository,
) : BlocksService {
    override fun getTodayData(userId: String): BlocksTodayResponse {
        val userUuid = Uuid.parse(userId)
        val now = Clock.System.now()
        val today = now.toLocalDateTime(TimeZone.currentSystemDefault()).date

        val groups = groupRepository.findByUserId(userUuid)

        val todayEvents = groups.flatMap { group ->
            eventRepository.getMonthEvents(Uuid.parse(group.id), today.year, today.monthNumber)
                .filter { event ->
                    val eventDate = event.startAt.toLocalDateTime(TimeZone.currentSystemDefault()).date
                    eventDate == today
                }
                .map { event ->
                    TodayEventDto(
                        id = event.id,
                        title = event.title,
                        startTime = event.startAt.toString(),
                        allDay = event.isAllDay,
                    )
                }
        }

        val blockGroups = groups.map { group ->
            BlockGroupDto(
                id = group.id,
                name = group.name ?: group.type,
                memberCount = group.members.size,
                latestActivity = group.createdAt.toString(),
            )
        }

        return BlocksTodayResponse(
            todayEvents = todayEvents,
            groups = blockGroups,
        )
    }
}
