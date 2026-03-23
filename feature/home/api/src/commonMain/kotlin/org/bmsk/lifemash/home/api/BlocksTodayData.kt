package org.bmsk.lifemash.home.api

data class BlocksTodayData(
    val todayEvents: List<TodayEvent>,
    val groups: List<BlockGroup>,
)

data class TodayEvent(
    val id: String,
    val title: String,
    val startTime: String,
    val allDay: Boolean,
)

data class BlockGroup(
    val id: String,
    val name: String,
    val memberCount: Int,
    val latestActivity: String?,
)
