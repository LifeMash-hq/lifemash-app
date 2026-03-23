package org.bmsk.lifemash.home.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.serialization.Serializable
import org.bmsk.lifemash.home.api.BlockGroup
import org.bmsk.lifemash.home.api.BlocksTodayData
import org.bmsk.lifemash.home.api.TodayEvent
import org.bmsk.lifemash.home.domain.repository.BlocksTodayRepository

@Serializable
private data class BlocksTodayResponseDto(
    val todayEvents: List<TodayEventDto>,
    val groups: List<BlockGroupDto>,
)

@Serializable
private data class TodayEventDto(
    val id: String,
    val title: String,
    val startTime: String,
    val allDay: Boolean,
)

@Serializable
private data class BlockGroupDto(
    val id: String,
    val name: String,
    val memberCount: Int,
    val latestActivity: String?,
)

class BlocksTodayRepositoryImpl(
    private val client: HttpClient,
) : BlocksTodayRepository {

    override suspend fun getTodayData(): BlocksTodayData {
        val response = client.get("/api/v1/blocks/today").body<BlocksTodayResponseDto>()
        return BlocksTodayData(
            todayEvents = response.todayEvents.map {
                TodayEvent(id = it.id, title = it.title, startTime = it.startTime, allDay = it.allDay)
            },
            groups = response.groups.map {
                BlockGroup(id = it.id, name = it.name, memberCount = it.memberCount, latestActivity = it.latestActivity)
            },
        )
    }
}
