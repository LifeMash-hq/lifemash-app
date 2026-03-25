package org.bmsk.lifemash.comment

import org.bmsk.lifemash.fake.FakeCommentRepository
import org.bmsk.lifemash.fake.FakeFcmService
import org.bmsk.lifemash.fake.FakeGroupRepository
import org.bmsk.lifemash.model.calendar.CreateCommentRequest
import org.bmsk.lifemash.plugins.ForbiddenException
import java.util.*
import kotlin.test.*

class CommentServiceTest {

    private lateinit var commentRepo: FakeCommentRepository
    private lateinit var groupRepo: FakeGroupRepository
    private lateinit var fcmService: FakeFcmService
    private lateinit var service: CommentService

    private val userId = UUID.randomUUID()
    private val nonMemberId = UUID.randomUUID()
    private lateinit var groupId: String
    private val eventId = UUID.randomUUID().toString()

    @BeforeTest
    fun setUp() {
        commentRepo = FakeCommentRepository()
        groupRepo = FakeGroupRepository()
        fcmService = FakeFcmService()
        service = CommentServiceImpl(commentRepo, groupRepo, fcmService)
        groupId = groupRepo.create(userId, "COUPLE", "테스트그룹").id
    }

    @Test
    fun `그룹 멤버가 댓글 목록을 조회한다`() {
        // Given
        commentRepo.create(UUID.fromString(eventId), userId, "첫 댓글")
        commentRepo.create(UUID.fromString(eventId), userId, "두번째 댓글")

        // When
        val comments = service.getComments(groupId, userId.toString(), eventId)

        // Then
        assertEquals(2, comments.size)
    }

    @Test
    fun `비멤버가 댓글 조회 시 ForbiddenException이 발생한다`() {
        // When & Then
        assertFailsWith<ForbiddenException> {
            service.getComments(groupId, nonMemberId.toString(), eventId)
        }
    }

    @Test
    fun `댓글 생성 시 FCM 알림이 발송된다`() {
        // Given
        val request = CreateCommentRequest(content = "좋은 일정이네요!")

        // When
        service.create(groupId, userId.toString(), eventId, request)

        // Then
        assertEquals(1, fcmService.notifications.size)
        assertEquals("새 댓글", fcmService.notifications[0].title)
        assertEquals("좋은 일정이네요!", fcmService.notifications[0].body)
    }

    @Test
    fun `비멤버가 댓글 생성 시 ForbiddenException이 발생한다`() {
        // When & Then
        assertFailsWith<ForbiddenException> {
            service.create(groupId, nonMemberId.toString(), eventId, CreateCommentRequest(content = "댓글"))
        }
    }

    @Test
    fun `작성자가 자신의 댓글을 삭제한다`() {
        // Given
        val comment = service.create(groupId, userId.toString(), eventId, CreateCommentRequest(content = "삭제할 댓글"))

        // When
        service.delete(groupId, userId.toString(), comment.id)

        // Then
        assertEquals(0, service.getComments(groupId, userId.toString(), eventId).size)
    }

    @Test
    fun `작성자가 아닌 사용자가 댓글 삭제 시 ForbiddenException이 발생한다`() {
        // Given
        val otherUserId = UUID.randomUUID()
        groupRepo.join(otherUserId, groupRepo.findById(UUID.fromString(groupId))!!.inviteCode)
        val comment = service.create(groupId, userId.toString(), eventId, CreateCommentRequest(content = "댓글"))

        // When & Then
        assertFailsWith<ForbiddenException> {
            service.delete(groupId, otherUserId.toString(), comment.id)
        }
    }
}
