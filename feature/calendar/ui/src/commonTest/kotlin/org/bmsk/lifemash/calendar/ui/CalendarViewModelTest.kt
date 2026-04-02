package org.bmsk.lifemash.calendar.ui

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import org.bmsk.lifemash.calendar.domain.model.Event
import org.bmsk.lifemash.calendar.domain.model.EventVisibility
import org.bmsk.lifemash.calendar.domain.model.Group
import org.bmsk.lifemash.calendar.domain.model.GroupType
import org.bmsk.lifemash.calendar.domain.repository.EventRepository
import org.bmsk.lifemash.calendar.domain.repository.GroupRepository
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class CalendarViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val now = Clock.System.now()

    private val testGroup = Group(
        id = "group-1", name = "테스트 커플", type = GroupType.COUPLE,
        maxMembers = 2, inviteCode = "ABCD1234", members = emptyList(), createdAt = now,
    )

    private val testEvent = Event(
        id = "event-1", groupId = "group-1", authorId = "user-1",
        title = "테스트 일정", description = null, location = null,
        startAt = now, endAt = null, isAllDay = false, color = null,
        createdAt = now, updatedAt = now,
    )

    private var groupsResult: List<Group> = listOf(testGroup)
    private var eventsResult: List<Event> = listOf(testEvent)

    private val fakeEventRepository = object : EventRepository {
        override suspend fun getMonthEvents(groupId: String, year: Int, month: Int): List<Event> = eventsResult
        override suspend fun createEvent(
            groupId: String,
            title: String,
            description: String?,
            location: String?,
            startAt: Instant,
            endAt: Instant?,
            isAllDay: Boolean,
            color: String?,
            visibility: EventVisibility,
        ): Event = testEvent

        override suspend fun updateEvent(
            groupId: String,
            eventId: String,
            title: String?,
            description: String?,
            location: String?,
            startAt: Instant?,
            endAt: Instant?,
            isAllDay: Boolean?,
            color: String?,
            visibility: EventVisibility?,
        ): Event = testEvent

        override suspend fun deleteEvent(groupId: String, eventId: String) {}
    }

    private val fakeGroupRepository = object : GroupRepository {
        override suspend fun createGroup(type: GroupType, name: String?): Group = testGroup
        override suspend fun joinGroup(inviteCode: String): Group = testGroup
        override suspend fun getMyGroups(): List<Group> = groupsResult
        override suspend fun getGroup(groupId: String): Group = testGroup
        override suspend fun deleteGroup(groupId: String) {}
        override suspend fun updateGroupName(groupId: String, name: String): Group = testGroup
    }

    private fun createViewModel() = CalendarViewModel(
        eventRepository = fakeEventRepository,
        groupRepository = fakeGroupRepository,
    )

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        groupsResult = listOf(testGroup)
        eventsResult = listOf(testEvent)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadGroups 호출 후 그룹과 이벤트가 로드된다`() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        viewModel.loadGroups()

        val state = viewModel.uiState.value
        assertEquals(1, state.groups.size)
        assertEquals(false, state.isLoading)
        assertEquals(CalendarOverlay.None, state.overlay)
    }

    @Test
    fun `그룹이 없으면 selectedGroup이 null이다`() = runTest(testDispatcher) {
        groupsResult = emptyList()
        val viewModel = createViewModel()

        viewModel.loadGroups()

        assertNull(viewModel.uiState.value.selectedGroup)
    }

    @Test
    fun `날짜 선택 시 selectedDate가 업데이트된다`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        viewModel.loadGroups()
        val date = LocalDate(2026, 3, 15)

        viewModel.selectDate(date)

        assertEquals(date, viewModel.uiState.value.selectedDate)
    }

    @Test
    fun `월 변경 시 currentYear과 currentMonth가 업데이트된다`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        viewModel.loadGroups()

        viewModel.changeMonth("group-1", 2026, 4)

        val state = viewModel.uiState.value
        assertEquals(2026, state.currentYear)
        assertEquals(4, state.currentMonth)
    }

    @Test
    fun `showOverlay로 EventDetail을 설정하면 overlay가 변경된다`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        viewModel.loadGroups()

        viewModel.showOverlay(CalendarOverlay.EventDetail(testEvent))

        val overlay = viewModel.uiState.value.overlay
        assertTrue(overlay is CalendarOverlay.EventDetail)
    }

    @Test
    fun `dismissOverlay로 overlay가 None이 된다`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        viewModel.loadGroups()
        viewModel.showOverlay(CalendarOverlay.GroupRename)

        viewModel.dismissOverlay()

        assertEquals(CalendarOverlay.None, viewModel.uiState.value.overlay)
    }
}
