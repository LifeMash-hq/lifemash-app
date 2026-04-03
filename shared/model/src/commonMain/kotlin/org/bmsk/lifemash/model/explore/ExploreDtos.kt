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

/** 탐색 > 공개 일정 카드 */
@Serializable
data class PublicEventDto(
    val id: String,
    val title: String,
    val startAt: String,
    val endAt: String? = null,
    val color: String? = null,
    val authorNickname: String? = null,
    val attendeeCount: Int = 0,
)

/** 탐색 > 활동 히트맵 — 날짜별 이벤트 수 */
@Serializable
data class HeatmapDayDto(
    val date: String,       // "2026-04-03"
    val eventCount: Int,
)

/** 탐색 > 팔로우 제안 유저 */
@Serializable
data class UserSuggestionDto(
    val id: String,
    val nickname: String,
    val profileImage: String? = null,
    val mutualFollowCount: Int = 0,
)
