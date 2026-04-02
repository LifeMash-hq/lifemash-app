package org.bmsk.lifemash.model.follow

import kotlinx.serialization.Serializable

/**
 * 사용자 기본 정보 요약 데이터
 * 탐색, 검색, 팔로우 목록 조회 등에서 공통으로 사용됩니다.
 */
@Serializable
data class UserSummaryDto(
    val id: String,
    val nickname: String,
    val profileImage: String? = null,
)
