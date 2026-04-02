package org.bmsk.lifemash.model.calendar

import kotlin.time.Instant
import kotlinx.serialization.Serializable

/**
 * 개별 이벤트 코멘트를 표현하는 DTO 클래스
 * @property id 코멘트의 고유 식별자 (UUID)
 * @property eventId 해당 코멘트가 달린 이벤트 식별자
 * @property authorId 코멘트 작성자 ID
 * @property authorNickname 작성자의 닉네임
 * @property authorProfileImage 작성자의 프로필 이미지 (선택)
 * @property content 내용
 * @property createdAt 작성 일시
 */
@Serializable
data class CommentDto(
    val id: String,
    val eventId: String,
    val authorId: String,
    val authorNickname: String,
    val authorProfileImage: String? = null,
    val content: String,
    val createdAt: Instant,
)

@Serializable
data class CreateCommentRequest(val content: String)
