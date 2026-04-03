package org.bmsk.lifemash.model.calendar

import kotlinx.serialization.Serializable
import kotlin.time.Instant

/**
 * 이벤트 참가자 정보를 담는 DTO 클래스
 * @property id 유저 고유 ID
 * @property nickname 유저 닉네임
 * @property profileImage 유저 프로필 이미지 URL (선택)
 */
@Serializable
data class EventAttendeeDto(
    val id: String,
    val nickname: String,
    val profileImage: String? = null,
    val status: String = "attending",  // "attending" | "maybe" | "declined"
)

/**
 * 이벤트 상세 조회 API의 응답을 구성하는 DTO
 * UI에서 렌더링에 필요한 댓글(Comments) 및 참가자 목록(Attendees)과 참가 여부 상태를 모두 포함합니다.
 */
@Serializable
data class EventDetailDto(
    val id: String,
    val groupId: String,
    val title: String,
    val description: String?,
    val startAt: Instant,
    val endAt: Instant?,
    val isAllDay: Boolean,
    val location: String?,
    val imageEmoji: String?,
    val authorNickname: String?,
    val attendees: List<EventAttendeeDto>,
    val comments: List<CommentDto>,
    val isJoined: Boolean,
)

@Serializable
data class ToggleJoinResponse(
    val isJoined: Boolean
)
