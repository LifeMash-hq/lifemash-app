package org.bmsk.lifemash.home.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import org.bmsk.lifemash.home.api.BlockGroup
import org.bmsk.lifemash.home.api.BlocksTodayData
import org.bmsk.lifemash.home.api.TodayEvent
import org.bmsk.lifemash.home.domain.repository.BlocksTodayRepository
import org.bmsk.lifemash.model.blocks.BlocksTodayResponse

class BlocksTodayRepositoryImpl(
    private val client: HttpClient,
) : BlocksTodayRepository {

    override suspend fun getTodayData(): BlocksTodayData {
        val response = client.get("/api/v1/blocks/today").body<BlocksTodayResponse>()
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
