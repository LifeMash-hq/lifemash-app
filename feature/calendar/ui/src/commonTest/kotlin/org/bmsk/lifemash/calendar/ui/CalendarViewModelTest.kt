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
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import org.bmsk.lifemash.calendar.domain.model.Event
import org.bmsk.lifemash.calendar.domain.model.Group
import org.bmsk.lifemash.calendar.domain.model.GroupType
import org.bmsk.lifemash.calendar.domain.repository.CreateEventRequest
import org.bmsk.lifemash.calendar.domain.repository.EventRepository
import org.bmsk.lifemash.calendar.domain.repository.GroupRepository
import org.bmsk.lifemash.calendar.domain.repository.UpdateEventRequest
import org.bmsk.lifemash.calendar.domain.usecase.GetMonthEventsUseCase
import org.bmsk.lifemash.calendar.domain.usecase.GetMyGroupsUseCase
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

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

    private val fakeGroupRepo = object : GroupRepository {
        override suspend fun getMyGroups(): List<Group> = groupsResult
        override suspend fun getGroup(groupId: String): Group = testGroup
        override suspend fun createGroup(type: GroupType, name: String?): Group = testGroup
        override suspend fun joinGroup(inviteCode: String): Group = testGroup
        override suspend fun deleteGroup(groupId: String) {}
    }

    private val fakeEventRepo = object : EventRepository {
        override fun getMonthEvents(groupId: String, year: Int, month: Int): Flow<List<Event>> =
            flowOf(eventsResult)
        override suspend fun createEvent(groupId: String, request: CreateEventRequest): Event = testEvent
        override suspend fun updateEvent(groupId: String, eventId: String, request: UpdateEventRequest): Event = testEvent
        override suspend fun deleteEvent(groupId: String, eventId: String) {}
    }

    private fun createViewModel() = CalendarViewModel(
        getMonthEventsUseCase = GetMonthEventsUseCase(fakeEventRepo),
        getMyGroupsUseCase = GetMyGroupsUseCase(fakeGroupRepo),
    )

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        groupsResult = listOf(testGroup)
        eventsResult = listOf(testEvent)
    }

    @AfterTest
    fun tearDown() { Dispatchers.resetMain() }

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
