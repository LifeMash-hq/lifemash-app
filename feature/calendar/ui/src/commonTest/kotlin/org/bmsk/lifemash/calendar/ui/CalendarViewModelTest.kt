package org.bmsk.lifemash.calendar.ui

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
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
import org.bmsk.lifemash.calendar.domain.usecase.UpdateGroupNameUseCase
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
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

    private val fakeUpdateGroupName = object : UpdateGroupNameUseCase {
        override suspend fun invoke(groupId: String, name: String): Group = testGroup
    }

    private fun createViewModel() = CalendarViewModel(
        getMonthEventsUseCase = fakeGetMonthEvents,
        getMyGroupsUseCase = fakeGetMyGroups,
        createGroupUseCase = fakeCreateGroup,
        joinGroupUseCase = fakeJoinGroup,
        createEventUseCase = fakeCreateEvent,
        updateEventUseCase = fakeUpdateEvent,
        deleteEventUseCase = fakeDeleteEvent,
        updateGroupNameUseCase = fakeUpdateGroupName,
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
    fun `초기 상태는 Loading이다`() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        assertIs<CalendarUiState.Loading>(viewModel.uiState.value)
    }

    @Test
    fun `loadGroups 호출 시 Loaded 상태가 된다`() = runTest(testDispatcher) {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.loadGroups()

        // Then
        val state = assertIs<CalendarUiState.Loaded>(viewModel.uiState.value)
        assertEquals(1, state.groups.size)
        assertEquals(CalendarOverlay.None, state.overlay)
    }

    @Test
    fun `그룹이 없으면 selectedGroup이 null이다`() = runTest(testDispatcher) {
        // Given
        groupsResult = emptyList()
        val viewModel = createViewModel()

        // When
        viewModel.loadGroups()

        // Then
        val state = assertIs<CalendarUiState.Loaded>(viewModel.uiState.value)
        assertNull(state.selectedGroup)
    }

    @Test
    fun `날짜 선택 시 selectedDate가 업데이트된다`() = runTest(testDispatcher) {
        // Given
        val viewModel = createViewModel()
        viewModel.loadGroups()
        val date = LocalDate(2026, 3, 15)

        // When
        viewModel.selectDate(date)

        // Then
        val state = assertIs<CalendarUiState.Loaded>(viewModel.uiState.value)
        assertEquals(date, state.selectedDate)
    }

    @Test
    fun `월 변경 시 currentYear과 currentMonth가 업데이트된다`() = runTest(testDispatcher) {
        // Given
        val viewModel = createViewModel()
        viewModel.loadGroups()

        // When
        viewModel.changeMonth(2026, 4)

        // Then
        val state = assertIs<CalendarUiState.Loaded>(viewModel.uiState.value)
        assertEquals(2026, state.currentYear)
        assertEquals(4, state.currentMonth)
    }

    @Test
    fun `showOverlay로 EventDetail을 설정하면 overlay가 변경된다`() = runTest(testDispatcher) {
        // Given
        val viewModel = createViewModel()
        viewModel.loadGroups()

        // When
        viewModel.showOverlay(CalendarOverlay.EventDetail(testEvent))

        // Then
        val state = assertIs<CalendarUiState.Loaded>(viewModel.uiState.value)
        assertIs<CalendarOverlay.EventDetail>(state.overlay)
    }

    @Test
    fun `dismissOverlay로 overlay가 None이 된다`() = runTest(testDispatcher) {
        // Given
        val viewModel = createViewModel()
        viewModel.loadGroups()
        viewModel.showOverlay(CalendarOverlay.GroupRename)

        // When
        viewModel.dismissOverlay()

        // Then
        val state = assertIs<CalendarUiState.Loaded>(viewModel.uiState.value)
        assertEquals(CalendarOverlay.None, state.overlay)
    }
}
