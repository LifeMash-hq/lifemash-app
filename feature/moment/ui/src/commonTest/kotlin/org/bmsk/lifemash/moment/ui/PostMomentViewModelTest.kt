@file:OptIn(kotlin.uuid.ExperimentalUuidApi::class)
package org.bmsk.lifemash.moment.ui

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.bmsk.lifemash.calendar.domain.model.Event
import org.bmsk.lifemash.calendar.domain.model.EventVisibility
import org.bmsk.lifemash.calendar.domain.model.Group
import org.bmsk.lifemash.calendar.domain.model.GroupType
import org.bmsk.lifemash.calendar.domain.repository.EventRepository
import org.bmsk.lifemash.calendar.domain.repository.GroupRepository
import org.bmsk.lifemash.moment.domain.model.MediaType
import org.bmsk.lifemash.moment.domain.model.Moment
import org.bmsk.lifemash.moment.domain.model.MomentMedia
import org.bmsk.lifemash.moment.domain.model.Visibility
import org.bmsk.lifemash.moment.domain.repository.MomentRepository
import org.bmsk.lifemash.moment.domain.usecase.CreateMomentUseCase
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalTime::class)
class PostMomentViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val now: Instant = Clock.System.now()

    private val testGroup = Group(
        id = "group-1", name = "테스트 그룹", type = GroupType.FRIENDS,
        maxMembers = 5, inviteCode = "CODE01", createdAt = now,
    )

    private val testMoment = Moment(
        id = "moment-1", authorId = "user-1", authorNickname = "테스터",
        visibility = Visibility.PUBLIC, media = emptyList(), createdAt = "2026-04-01",
    )

    private var createMomentShouldThrow = false
    private var lastCreatedCaption: String? = "NOT_SET"
    private var lastCreatedVisibility: Visibility? = null

    private val fakeMomentRepository = object : MomentRepository {
        override suspend fun create(
            eventId: String?, caption: String?,
            visibility: Visibility, media: List<MomentMedia>,
        ): Moment {
            if (createMomentShouldThrow) throw RuntimeException("업로드 실패")
            lastCreatedCaption = caption
            lastCreatedVisibility = visibility
            return testMoment
        }
        override suspend fun getUserMoments(userId: String): List<Moment> = emptyList()
        override suspend fun delete(momentId: String) {}
    }

    private val fakeGroupRepository = object : GroupRepository {
        override suspend fun createGroup(type: GroupType, name: String?): Group = testGroup
        override suspend fun joinGroup(inviteCode: String): Group = testGroup
        override suspend fun getMyGroups(): List<Group> = listOf(testGroup)
        override suspend fun getGroup(groupId: String): Group = testGroup
        override suspend fun deleteGroup(groupId: String) {}
        override suspend fun updateGroupName(groupId: String, name: String): Group = testGroup
    }

    private val fakeEventRepository = object : EventRepository {
        override suspend fun getMonthEvents(groupId: String, year: Int, month: Int): List<Event> =
            emptyList()
        override suspend fun createEvent(
            groupId: String, title: String, description: String?, location: String?,
            startAt: Instant, endAt: Instant?, isAllDay: Boolean, color: String?,
            visibility: EventVisibility,
        ): Event = throw NotImplementedError()
        override suspend fun updateEvent(
            groupId: String, eventId: String, title: String?, description: String?,
            location: String?, startAt: Instant?, endAt: Instant?,
            isAllDay: Boolean?, color: String?, visibility: EventVisibility?,
        ): Event = throw NotImplementedError()
        override suspend fun deleteEvent(groupId: String, eventId: String) {}
    }

    private fun createViewModel() = PostMomentViewModel(
        createMoment = CreateMomentUseCase(fakeMomentRepository),
        eventRepository = fakeEventRepository,
        groupRepository = fakeGroupRepository,
    )

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        createMomentShouldThrow = false
        lastCreatedCaption = "NOT_SET"
        lastCreatedVisibility = null
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ─── onCaptionChange ───────────────────────────────────────────────────────

    @Test
    fun `onCaptionChange 호출 시 caption이 업데이트된다`() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        viewModel.onCaptionChange("안녕하세요")

        assertEquals("안녕하세요", viewModel.form.value.caption)
    }

    @Test
    fun `onCaptionChange 200자 초과 입력 시 200자로 잘린다`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        val longText = "가".repeat(250)

        viewModel.onCaptionChange(longText)

        assertEquals(200, viewModel.form.value.caption.length)
    }

    // ─── onCycleVisibility ─────────────────────────────────────────────────────

    @Test
    fun `onCycleVisibility 호출 시 PUBLIC → FOLLOWERS → PRIVATE → PUBLIC 순환한다`() =
        runTest(testDispatcher) {
            val viewModel = createViewModel()
            assertEquals(Visibility.PUBLIC, viewModel.form.value.visibility)

            viewModel.onCycleVisibility()
            assertEquals(Visibility.FOLLOWERS, viewModel.form.value.visibility)

            viewModel.onCycleVisibility()
            assertEquals(Visibility.PRIVATE, viewModel.form.value.visibility)

            viewModel.onCycleVisibility()
            assertEquals(Visibility.PUBLIC, viewModel.form.value.visibility)
        }

    // ─── onTagEvent ────────────────────────────────────────────────────────────

    @Test
    fun `onTagEvent 호출 시 eventId와 eventTitle이 설정된다`() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        viewModel.onTagEvent("event-1", "생일 파티")

        assertEquals("event-1", viewModel.form.value.eventId)
        assertEquals("생일 파티", viewModel.form.value.eventTitle)
    }

    @Test
    fun `onTagEvent null 호출 시 태그가 해제된다`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        viewModel.onTagEvent("event-1", "생일 파티")

        viewModel.onTagEvent(null, null)

        assertNull(viewModel.form.value.eventId)
        assertNull(viewModel.form.value.eventTitle)
    }

    // ─── onAddMedia / onRemoveMedia ────────────────────────────────────────────

    @Test
    fun `onAddMedia 호출 시 미디어가 추가된다`() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        viewModel.onAddMedia("file://photo.jpg", MediaType.IMAGE)

        assertEquals(1, viewModel.form.value.media.size)
        assertEquals(MediaType.IMAGE, viewModel.form.value.media[0].mediaType)
    }

    @Test
    fun `onAddMedia 10개 초과 시 더 이상 추가되지 않는다`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        repeat(10) { viewModel.onAddMedia("file://photo$it.jpg", MediaType.IMAGE) }

        viewModel.onAddMedia("file://photo_overflow.jpg", MediaType.IMAGE)

        assertEquals(10, viewModel.form.value.media.size)
    }

    @Test
    fun `onRemoveMedia 호출 시 해당 미디어가 제거된다`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        viewModel.onAddMedia("file://photo.jpg", MediaType.IMAGE)
        val mediaId = viewModel.form.value.media[0].id

        viewModel.onRemoveMedia(mediaId)

        assertEquals(0, viewModel.form.value.media.size)
    }

    // ─── onSubmit ──────────────────────────────────────────────────────────────

    @Test
    fun `canSubmit이 false이면 onSubmit이 아무 것도 하지 않는다`() = runTest(testDispatcher) {
        val viewModel = createViewModel() // caption=""이고 media=[]
        var uploadCalled = false

        viewModel.onSubmit { uploadCalled = true; "url" }

        assertIs<PostMomentUiState.Idle>(viewModel.uiState.value)
        assertEquals(false, uploadCalled)
    }

    @Test
    fun `onSubmit 성공 시 Success 상태가 된다`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        viewModel.onCaptionChange("오늘의 순간")

        viewModel.onSubmit { "http://cdn.example.com/media.jpg" }

        assertIs<PostMomentUiState.Success>(viewModel.uiState.value)
        assertEquals("오늘의 순간", lastCreatedCaption)
    }

    @Test
    fun `onSubmit 실패 시 Error 상태가 된다`() = runTest(testDispatcher) {
        createMomentShouldThrow = true
        val viewModel = createViewModel()
        viewModel.onCaptionChange("테스트")

        viewModel.onSubmit { "http://cdn.example.com/media.jpg" }

        val state = viewModel.uiState.value
        assertIs<PostMomentUiState.Error>(state)
        assertEquals("업로드 실패", state.message)
    }

    @Test
    fun `onSubmit 미디어가 있을 때 uploadMedia가 각 항목마다 호출된다`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        viewModel.onCaptionChange("미디어 테스트")
        viewModel.onAddMedia("file://photo1.jpg", MediaType.IMAGE)
        viewModel.onAddMedia("file://photo2.jpg", MediaType.IMAGE)
        var uploadCallCount = 0

        viewModel.onSubmit { uploadCallCount++; "http://cdn.example.com/photo$uploadCallCount.jpg" }

        assertEquals(2, uploadCallCount)
        assertIs<PostMomentUiState.Success>(viewModel.uiState.value)
    }

    // ─── onErrorDismissed ──────────────────────────────────────────────────────

    @Test
    fun `onErrorDismissed 호출 시 Idle 상태로 돌아온다`() = runTest(testDispatcher) {
        createMomentShouldThrow = true
        val viewModel = createViewModel()
        viewModel.onCaptionChange("테스트")
        viewModel.onSubmit { "url" }
        assertIs<PostMomentUiState.Error>(viewModel.uiState.value)

        viewModel.onErrorDismissed()

        assertIs<PostMomentUiState.Idle>(viewModel.uiState.value)
    }
}
