package org.bmsk.lifemash.comment

import org.bmsk.lifemash.group.MembershipGuard
import org.bmsk.lifemash.model.calendar.CommentDto
import org.bmsk.lifemash.model.calendar.CreateCommentRequest
import org.bmsk.lifemash.notification.FcmService
import kotlin.uuid.Uuid

/**
 * 댓글 비즈니스 로직 서비스.
 * 그룹 멤버십 검증 + 댓글 CRUD + 알림 발송을 담당.
 * 인가는 MembershipGuard.requireByEvent에 위임 — groupId 해소 로직을 직접 다루지 않음.
 */
class CommentServiceImpl(
    private val commentRepository: CommentRepository,
    private val membershipGuard: MembershipGuard,
    private val fcmService: FcmService,
) : CommentService {
    /** 일정에 달린 댓글 목록 조회 */
    override fun getComments(userId: String, eventId: String): List<CommentDto> {
        membershipGuard.requireByEvent(eventId, userId)
        return commentRepository.getByEventId(Uuid.parse(eventId))
    }

    /** 댓글 생성 후 그룹 내 다른 멤버에게 "새 댓글" 알림 발송 */
    override fun create(userId: String, eventId: String, request: CreateCommentRequest): CommentDto {
        val groupId = membershipGuard.requireByEvent(eventId, userId)
        val comment = commentRepository.create(Uuid.parse(eventId), Uuid.parse(userId), request.content)
        fcmService.notifyGroupExcept(Uuid.parse(groupId), Uuid.parse(userId), "새 댓글", request.content)
        return comment
    }

    /** 댓글 삭제 — 작성자 본인만 가능 (Repository에서 검증) */
    override fun delete(userId: String, eventId: String, commentId: String) {
        membershipGuard.requireByEvent(eventId, userId)
        commentRepository.delete(Uuid.parse(commentId), Uuid.parse(userId))
    }
}
