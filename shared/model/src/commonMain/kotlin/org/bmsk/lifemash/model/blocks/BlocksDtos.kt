package org.bmsk.lifemash.model.blocks

import kotlinx.serialization.Serializable

@Serializable
data class BlocksTodayResponse(
    val todayEvents: List<TodayEventDto>,
    val groups: List<BlockGroupDto>,
)

@Serializable
data class TodayEventDto(
    val id: String,
    val title: String,
    val startTime: String,
    val allDay: Boolean,
)

@Serializable
data class BlockGroupDto(
    val id: String,
    val name: String,
    val memberCount: Int,
    val latestActivity: String?,
)
