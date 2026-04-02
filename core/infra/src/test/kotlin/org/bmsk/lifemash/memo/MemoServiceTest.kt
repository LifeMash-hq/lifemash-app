package org.bmsk.lifemash.memo

import org.bmsk.lifemash.fake.FakeChecklistItemRepository
import org.bmsk.lifemash.fake.FakeFcmService
import org.bmsk.lifemash.fake.FakeMembershipGuard
import org.bmsk.lifemash.fake.FakeMemoRepository
import org.bmsk.lifemash.model.memo.CreateMemoRequest
import org.bmsk.lifemash.model.memo.SyncChecklistItemEntry
import org.bmsk.lifemash.model.memo.SyncChecklistRequest
import org.bmsk.lifemash.model.memo.UpdateMemoRequest
import org.bmsk.lifemash.plugins.BadRequestException
import org.bmsk.lifemash.plugins.ForbiddenException
import org.bmsk.lifemash.plugins.NotFoundException
import kotlin.test.*
import kotlin.uuid.Uuid

class MemoServiceTest {
    private lateinit var service: MemoServiceImpl
    private lateinit var memoRepo: FakeMemoRepository
    private lateinit var checklistRepo: FakeChecklistItemRepository
    private lateinit var membershipGuard: FakeMembershipGuard
    private lateinit var fcmService: FakeFcmService

    private val groupId = Uuid.random()
    private val userId = Uuid.random()

    @BeforeTest
    fun setUp() {
        memoRepo = FakeMemoRepository()
        checklistRepo = FakeChecklistItemRepository()
        membershipGuard = FakeMembershipGuard()
        fcmService = FakeFcmService()
        service = MemoServiceImpl(memoRepo, checklistRepo, membershipGuard, fcmService)
        membershipGuard.addMember(groupId, userId)
    }

    // region CRUD

    @Test
    fun `메모를_생성하면_조회할_수_있다`() {
        val memo = service.create(
            groupId.toString(), userId.toString(),
            CreateMemoRequest(title = "테스트 메모", content = "내용"),
        )

        val result = service.getGroupMemos(groupId.toString(), userId.toString())
        assertEquals(1, result.size)
        assertEquals("테스트 메모", result[0].title)
        assertEquals(memo.id, result[0].id)
    }

    @Test
    fun `메모를_수정하면_변경사항이_반영된다`() {
        val memo = service.create(
            groupId.toString(), userId.toString(),
            CreateMemoRequest(title = "원래 제목"),
        )

        val updated = service.update(
            groupId.toString(), memo.id, userId.toString(),
            UpdateMemoRequest(title = "수정된 제목"),
        )

        assertEquals("수정된 제목", updated.title)
    }

    @Test
    fun `메모를_삭제하면_조회되지_않는다`() {
        val memo = service.create(
            groupId.toString(), userId.toString(),
            CreateMemoRequest(title = "삭제할 메모"),
        )

        service.delete(groupId.toString(), memo.id, userId.toString())

        val result = service.getGroupMemos(groupId.toString(), userId.toString())
        assertTrue(result.isEmpty())
    }

    @Test
    fun `존재하지_않는_메모_조회_시_NotFoundException`() {
        assertFailsWith<NotFoundException> {
            service.getMemo(groupId.toString(), Uuid.random().toString(), userId.toString())
        }
    }

    @Test
    fun `존재하지_않는_메모_삭제_시_NotFoundException`() {
        assertFailsWith<NotFoundException> {
            service.delete(groupId.toString(), Uuid.random().toString(), userId.toString())
        }
    }

    // endregion

    // region 멤버십 검증

    @Test
    fun `비멤버가_메모를_조회하면_ForbiddenException`() {
        assertFailsWith<ForbiddenException> {
            service.getGroupMemos(groupId.toString(), Uuid.random().toString())
        }
    }

    @Test
    fun `비멤버가_메모를_생성하면_ForbiddenException`() {
        assertFailsWith<ForbiddenException> {
            service.create(
                groupId.toString(), Uuid.random().toString(),
                CreateMemoRequest(title = "불법 메모"),
            )
        }
    }

    // endregion

    // region 핀 제한

    @Test
    fun `핀_메모는_최대_5개까지_가능하다`() {
        repeat(5) { i ->
            service.create(
                groupId.toString(), userId.toString(),
                CreateMemoRequest(title = "핀 $i", isPinned = true),
            )
        }

        assertFailsWith<BadRequestException> {
            service.create(
                groupId.toString(), userId.toString(),
                CreateMemoRequest(title = "핀 6", isPinned = true),
            )
        }
    }

    @Test
    fun `비핀_메모를_핀으로_변경_시_제한_적용`() {
        repeat(5) { i ->
            service.create(
                groupId.toString(), userId.toString(),
                CreateMemoRequest(title = "핀 $i", isPinned = true),
            )
        }

        val unpinned = service.create(
            groupId.toString(), userId.toString(),
            CreateMemoRequest(title = "비핀 메모"),
        )

        assertFailsWith<BadRequestException> {
            service.update(
                groupId.toString(), unpinned.id, userId.toString(),
                UpdateMemoRequest(isPinned = true),
            )
        }
    }

    @Test
    fun `이미_핀인_메모를_핀으로_업데이트하면_제한에_걸리지_않는다`() {
        repeat(5) { i ->
            service.create(
                groupId.toString(), userId.toString(),
                CreateMemoRequest(title = "핀 $i", isPinned = true),
            )
        }

        val memos = service.getGroupMemos(groupId.toString(), userId.toString())
        val firstPinned = memos.first { it.isPinned }

        // 이미 핀인 메모에 isPinned=true → 추가 슬롯 소비하지 않음
        val updated = service.update(
            groupId.toString(), firstPinned.id, userId.toString(),
            UpdateMemoRequest(isPinned = true, title = "변경됨"),
        )
        assertEquals("변경됨", updated.title)
    }

    // endregion

    // region 검색

    @Test
    fun `제목으로_검색할_수_있다`() {
        service.create(groupId.toString(), userId.toString(), CreateMemoRequest(title = "회의록", content = "내용"))
        service.create(groupId.toString(), userId.toString(), CreateMemoRequest(title = "장보기 목록", content = "우유"))

        val result = service.search(groupId.toString(), userId.toString(), "회의")
        assertEquals(1, result.size)
        assertEquals("회의록", result[0].title)
    }

    @Test
    fun `내용으로_검색할_수_있다`() {
        service.create(groupId.toString(), userId.toString(), CreateMemoRequest(title = "메모", content = "우유 사기"))

        val result = service.search(groupId.toString(), userId.toString(), "우유")
        assertEquals(1, result.size)
    }

    // endregion

    // region 체크리스트 동기화

    @Test
    fun `체크리스트_항목을_동기화할_수_있다`() {
        val memo = service.create(
            groupId.toString(), userId.toString(),
            CreateMemoRequest(title = "체크리스트", isChecklist = true),
        )

        val items = service.syncChecklist(
            groupId.toString(), memo.id, userId.toString(),
            SyncChecklistRequest(
                items = listOf(
                    SyncChecklistItemEntry(content = "항목1", sortOrder = 0),
                    SyncChecklistItemEntry(content = "항목2", sortOrder = 1),
                )
            ),
        )

        assertEquals(2, items.size)
        assertEquals("항목1", items[0].content)
        assertEquals("항목2", items[1].content)
    }

    @Test
    fun `존재하지_않는_메모에_체크리스트_동기화_시_NotFoundException`() {
        assertFailsWith<NotFoundException> {
            service.syncChecklist(
                groupId.toString(), Uuid.random().toString(), userId.toString(),
                SyncChecklistRequest(items = emptyList()),
            )
        }
    }

    // endregion

    // region FCM 알림

    @Test
    fun `메모_생성_시_FCM_알림이_발송된다`() {
        service.create(
            groupId.toString(), userId.toString(),
            CreateMemoRequest(title = "알림 테스트"),
        )

        assertEquals(1, fcmService.notifications.size)
        assertEquals("새 메모", fcmService.notifications[0].title)
        assertEquals("알림 테스트", fcmService.notifications[0].body)
    }

    @Test
    fun `메모_수정_시_FCM_알림이_발송된다`() {
        val memo = service.create(
            groupId.toString(), userId.toString(),
            CreateMemoRequest(title = "원래"),
        )
        fcmService.notifications.clear()

        service.update(
            groupId.toString(), memo.id, userId.toString(),
            UpdateMemoRequest(title = "수정"),
        )

        assertEquals(1, fcmService.notifications.size)
        assertEquals("메모 수정", fcmService.notifications[0].title)
    }

    // endregion
}
