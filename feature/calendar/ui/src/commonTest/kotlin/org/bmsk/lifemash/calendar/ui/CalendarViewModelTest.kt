package org.bmsk.lifemash.calendar.ui

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import org.bmsk.lifemash.calendar.domain.model.Event
import org.bmsk.lifemash.calendar.domain.model.Group
import org.bmsk.lifemash.calendar.domain.model.GroupType
import org.bmsk.lifemash.calendar.domain.repository.CreateEventRequest
import org.bmsk.lifemash.calendar.domain.repository.UpdateEventRequest
import org.bmsk.lifemash.calendar.domain.usecase.CreateEventUseCase
import org.bmsk.lifemash.calendar.domain.usecase.CreateGroupUseCase
import org.bmsk.lifemash.calendar.domain.usecase.DeleteEventUseCase
import org.bmsk.lifemash.calendar.domain.usecase.GetMonthEventsUseCase
import org.bmsk.lifemash.calendar.domain.usecase.GetMyGroupsUseCase
import org.bmsk.lifemash.calendar.domain.usecase.JoinGroupUseCase
import org.bmsk.lifemash.calendar.domain.usecase.UpdateEventUseCase
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.time.Clock

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
        title = "테스트 일정", description = null,
        startAt = now, endAt = null, isAllDay = false, color = null,
        createdAt = now, updatedAt = now,
    )

    private var groupsResult: List<Group> = listOf(testGroup)
    private var eventsResult: List<Event> = listOf(testEvent)

    private val fakeGetMonthEvents = object : GetMonthEventsUseCase {
        override fun invoke(groupId: String, year: Int, month: Int): Flow<List<Event>> =
            flowOf(eventsResult)
    }

    private val fakeGetMyGroups = object : GetMyGroupsUseCase {
        override suspend fun invoke(): List<Group> = groupsResult
    }

    private val fakeCreateGroup = object : CreateGroupUseCase {
        override suspend fun invoke(type: GroupType, name: String?): Group = testGroup
    }

    private val fakeJoinGroup = object : JoinGroupUseCase {
        override suspend fun invoke(inviteCode: String): Group = testGroup
    }

    private val fakeCreateEvent = object : CreateEventUseCase {
        override suspend fun invoke(groupId: String, request: CreateEventRequest): Event = testEvent
    }

    private val fakeUpdateEvent = object : UpdateEventUseCase {
        override suspend fun invoke(groupId: String, eventId: String, request: UpdateEventRequest): Event = testEvent
    }

    private val fakeDeleteEvent = object : DeleteEventUseCase {
        override suspend fun invoke(groupId: String, eventId: String) {}
    }

    private fun createViewModel() = CalendarViewModel(
        getMonthEventsUseCase = fakeGetMonthEvents,
        getMyGroupsUseCase = fakeGetMyGroups,
        createGroupUseCase = fakeCreateGroup,
        joinGroupUseCase = fakeJoinGroup,
        createEventUseCase = fakeCreateEvent,
        updateEventUseCase = fakeUpdateEvent,
        deleteEventUseCase = fakeDeleteEvent,
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
    fun `초기화 시 그룹을 로드하고 Loading이 해제된다`() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        val values = mutableListOf<CalendarUiState>()
        val job = backgroundScope.launch(testDispatcher) {
            viewModel.uiState.collect { values.add(it) }
        }

        assertFalse(values.last().isLoading)
        assertNotNull(values.last().groups)
        assertEquals(1, values.last().groups!!.size)
        job.cancel()
    }

    @Test
    fun `그룹이 없으면 selectedGroup이 null이다`() = runTest(testDispatcher) {
        groupsResult = emptyList()
        val viewModel = createViewModel()

        val values = mutableListOf<CalendarUiState>()
        val job = backgroundScope.launch(testDispatcher) {
            viewModel.uiState.collect { values.add(it) }
        }

        assertEquals(null, values.last().selectedGroup)
        job.cancel()
    }

    @Test
    fun `날짜 선택 시 selectedDate가 업데이트된다`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        val date = LocalDate(2026, 3, 15)

        viewModel.selectDate(date)

        assertEquals(date, viewModel.uiState.value.selectedDate)
    }

    @Test
    fun `월 변경 시 currentYear과 currentMonth가 업데이트된다`() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        viewModel.changeMonth(2026, 4)

        assertEquals(2026, viewModel.uiState.value.currentYear)
        assertEquals(4, viewModel.uiState.value.currentMonth)
    }
}
