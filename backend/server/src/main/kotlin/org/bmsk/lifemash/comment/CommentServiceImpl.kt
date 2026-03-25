package org.bmsk.lifemash.comment

import org.bmsk.lifemash.model.calendar.CommentDto
import org.bmsk.lifemash.model.calendar.CreateCommentRequest
import org.bmsk.lifemash.group.GroupRepository
import org.bmsk.lifemash.notification.FcmService
import org.bmsk.lifemash.plugins.ForbiddenException
import org.bmsk.lifemash.validation.CommentContent
import java.util.*

/**
 * 댓글 비즈니스 로직 서비스.
 * 그룹 멤버십 검증 + 댓글 CRUD + 알림 발송을 담당.
 */
class CommentServiceImpl(
    private val commentRepository: CommentRepository,
    private val groupRepository: GroupRepository,
    private val fcmService: FcmService,
) : CommentService {
    /** 일정에 달린 댓글 목록 조회 */
    override fun getComments(groupId: String, userId: String, eventId: String): List<CommentDto> {
        requireMembership(groupId, userId)
        return commentRepository.getByEventId(UUID.fromString(eventId))
    }

    /** 댓글 생성 후 그룹 내 다른 멤버에게 "새 댓글" 알림 발송 */
    override fun create(groupId: String, userId: String, eventId: String, request: CreateCommentRequest): CommentDto {
        CommentContent.of(request.content)
        requireMembership(groupId, userId)
        val comment = commentRepository.create(UUID.fromString(eventId), UUID.fromString(userId), request.content)
        fcmService.notifyGroupExcept(UUID.fromString(groupId), UUID.fromString(userId), "새 댓글", request.content)
        return comment
    }

    /** 댓글 삭제 — 작성자 본인만 가능 (Repository에서 검증) */
    override fun delete(groupId: String, userId: String, commentId: String) {
        requireMembership(groupId, userId)
        commentRepository.delete(UUID.fromString(commentId), UUID.fromString(userId))
    }

    /** 그룹 멤버가 아니면 403 Forbidden */
    private fun requireMembership(groupId: String, userId: String) {
        if (!groupRepository.isMember(UUID.fromString(groupId), UUID.fromString(userId))) {
            throw ForbiddenException("Not a member of this group")
        }
    }
}
