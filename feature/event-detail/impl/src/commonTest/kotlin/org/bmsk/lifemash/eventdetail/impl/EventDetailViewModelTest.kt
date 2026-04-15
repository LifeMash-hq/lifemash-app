@file:OptIn(kotlin.time.ExperimentalTime::class)
package org.bmsk.lifemash.eventdetail.impl

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.bmsk.lifemash.domain.calendar.EventTiming
import org.bmsk.lifemash.domain.eventdetail.EventAttendee
import org.bmsk.lifemash.domain.eventdetail.EventComment
import org.bmsk.lifemash.domain.eventdetail.EventDetail
import org.bmsk.lifemash.domain.eventdetail.EventDetailRepository
import org.bmsk.lifemash.domain.usecase.eventdetail.AddEventCommentUseCase
import org.bmsk.lifemash.domain.usecase.eventdetail.GetEventDetailUseCase
import org.bmsk.lifemash.domain.usecase.eventdetail.ToggleEventJoinUseCase
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class EventDetailViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val now: Instant = Clock.System.now()

    private val testDetail = EventDetail(
        id = "event-1",
        groupId = "group-1",
        title = "테스트 일정",
        description = "설명",
        timing = EventTiming.Timed(start = now, end = now),
        location = "서울",
        imageEmoji = "🎉",
        sharedByNickname = "홍길동",
        attendees = listOf(
            EventAttendee(id = "user-1", nickname = "참석자A"),
            EventAttendee(id = "user-2", nickname = "참석자B"),
        ),
        comments = listOf(
            EventComment(id = "c-1", authorNickname = "댓글러", content = "좋아요!", createdAt = now),
        ),
        isJoined = false,
    )

    private var detailResult: EventDetail = testDetail
    private var toggleJoinShouldThrow = false
    private var addCommentShouldThrow = false

    private val fakeRepository = object : EventDetailRepository {
        override suspend fun getEventDetail(eventId: String): EventDetail = detailResult
        override suspend fun toggleJoin(eventId: String): Boolean {
            if (toggleJoinShouldThrow) throw RuntimeException("참여 실패")
            return true
        }
        override suspend fun addComment(eventId: String, content: String): EventComment {
            if (addCommentShouldThrow) throw RuntimeException("댓글 실패")
            return EventComment(
                id = "new-c",
                authorNickname = "나",
                content = content,
                createdAt = now,
            )
        }
    }

    private fun createViewModel(repository: EventDetailRepository = fakeRepository) =
        EventDetailViewModel(
            getEventDetailUseCase = GetEventDetailUseCase(repository),
            toggleEventJoinUseCase = ToggleEventJoinUseCase(repository),
            addEventCommentUseCase = AddEventCommentUseCase(repository),
        )

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        detailResult = testDetail
        toggleJoinShouldThrow = false
        addCommentShouldThrow = false
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ─── loadEvent ─────────────────────────────────────────────────────────────

    @Test
    fun `loadEvent 성공 시 Loaded 상태가 된다`() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        viewModel.loadEvent("event-1")

        val state = viewModel.uiState.value
        assertIs<EventDetailUiState.Loaded>(state)
        assertEquals("event-1", state.eventId)
        assertEquals("테스트 일정", state.title)
    }

    @Test
    fun `loadEvent 실패 시 Error 상태가 된다`() = runTest(testDispatcher) {
        val failingRepo = object : EventDetailRepository by fakeRepository {
            override suspend fun getEventDetail(eventId: String): EventDetail =
                throw RuntimeException("서버 오류")
        }
        val viewModel = createViewModel(repository = failingRepo)

        viewModel.loadEvent("event-1")

        val state = viewModel.uiState.value
        assertIs<EventDetailUiState.Error>(state)
        assertEquals("서버 오류", state.message)
    }

    @Test
    fun `loadEvent 시 attendees가 올바르게 매핑된다`() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        viewModel.loadEvent("event-1")

        val state = viewModel.uiState.value as EventDetailUiState.Loaded
        assertEquals(2, state.attendees.size)
        assertEquals("참석자A", state.attendees[0].nickname)
        assertEquals("참석자B", state.attendees[1].nickname)
    }

    @Test
    fun `loadEvent 시 comments가 올바르게 매핑된다`() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        viewModel.loadEvent("event-1")

        val state = viewModel.uiState.value as EventDetailUiState.Loaded
        assertEquals(1, state.comments.size)
        assertEquals("댓글러", state.comments[0].authorNickname)
        assertEquals("좋아요!", state.comments[0].content)
    }

    // ─── toggleJoin ────────────────────────────────────────────────────────────

    @Test
    fun `toggleJoin 호출 시 즉시 낙관적 업데이트된다`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        viewModel.loadEvent("event-1")
        val loaded = viewModel.uiState.value as EventDetailUiState.Loaded
        assertFalse(loaded.isJoined)

        viewModel.toggleJoin(loaded)

        val state = viewModel.uiState.value as EventDetailUiState.Loaded
        assertTrue(state.isJoined)
    }

    @Test
    fun `toggleJoin 실패 시 isJoined가 롤백된다`() = runTest(testDispatcher) {
        toggleJoinShouldThrow = true
        val viewModel = createViewModel()
        viewModel.loadEvent("event-1")
        val loaded = viewModel.uiState.value as EventDetailUiState.Loaded

        viewModel.toggleJoin(loaded)

        val state = viewModel.uiState.value as EventDetailUiState.Loaded
        assertFalse(state.isJoined) // 원상복구
    }

    // ─── addComment ────────────────────────────────────────────────────────────

    @Test
    fun `addComment 성공 시 comments 목록에 추가된다`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        viewModel.loadEvent("event-1")
        val loaded = viewModel.uiState.value as EventDetailUiState.Loaded
        assertEquals(1, loaded.comments.size)

        viewModel.addComment(loaded, "새 댓글")

        val state = viewModel.uiState.value as EventDetailUiState.Loaded
        assertEquals(2, state.comments.size)
        assertEquals("새 댓글", state.comments.last().content)
    }
}
