package org.bmsk.lifemash.model.explore

import kotlinx.serialization.Serializable

/**
 * 캘린더/탐색 페이지에서 노출되는 일정의 요약 정보
 */
@Serializable
data class EventSummaryDto(
    val id: String,
    val title: String,
    val startAt: String,
    val color: String? = null,
)
