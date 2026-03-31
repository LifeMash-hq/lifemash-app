package org.bmsk.lifemash.model.explore

import kotlinx.serialization.Serializable

@Serializable
data class EventSummaryDto(
    val id: String,
    val title: String,
    val startAt: String,
    val color: String? = null,
)
