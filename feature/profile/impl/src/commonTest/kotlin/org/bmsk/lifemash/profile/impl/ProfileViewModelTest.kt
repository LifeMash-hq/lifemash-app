@file:OptIn(kotlin.time.ExperimentalTime::class)
package org.bmsk.lifemash.profile.impl

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.bmsk.lifemash.domain.calendar.Event
import org.bmsk.lifemash.domain.calendar.EventVisibility
import org.bmsk.lifemash.domain.calendar.Group
import org.bmsk.lifemash.domain.calendar.GroupType
import org.bmsk.lifemash.domain.calendar.EventRepository
import org.bmsk.lifemash.domain.calendar.GroupRepository
import org.bmsk.lifemash.domain.profile.Moment
import org.bmsk.lifemash.domain.profile.ProfileSettings
import org.bmsk.lifemash.domain.profile.UserProfile
import org.bmsk.lifemash.domain.profile.ProfileRepository
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
class ProfileViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val now: Instant = Clock.System.now()

    private val testProfile = UserProfile(
        id = "user-1",
        email = "test@example.com",
        nickname = "테스터",
        isFollowing = false,
        followerCount = 10,
        followingCount = 5,
    )

    private val testGroup = Group(
        id = "group-1",
        name = "테스트 그룹",
        type = GroupType.FRIENDS,
        maxMembers = 5,
        inviteCode = "CODE01",
        createdAt = now,
    )

    private var followShouldThrow = false

    private val fakeProfileRepository = object : ProfileRepository {
        override fun getProfile(userId: String): Flow<UserProfile> = flowOf(testProfile)
        override suspend fun updateProfile(
            nickname: String?,
            bio: String?,
            profileImage: String?,
        ): UserProfile = testProfile
        override suspend fun follow(userId: String) {
            if (followShouldThrow) throw RuntimeException("팔로우 실패")
        }
        override suspend fun unfollow(userId: String) {
            if (followShouldThrow) throw RuntimeException("언팔로우 실패")
        }
        override fun getMoments(userId: String): Flow<List<Moment>> = flowOf(emptyList())
        override suspend fun getProfileSettings(): ProfileSettings = ProfileSettings()
        override suspend fun updateProfileSettings(settings: ProfileSettings) {}
    }

    private val fakeEventRepository = object : EventRepository {
        override suspend fun getMonthEvents(
            groupId: String,
            year: Int,
            month: Int,
        ): List<Event> = emptyList()
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
        ): Event = throw NotImplementedError()
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
        ): Event = throw NotImplementedError()
        override suspend fun deleteEvent(groupId: String, eventId: String) {}
    }

    private val fakeGroupRepository = object : GroupRepository {
        override suspend fun createGroup(type: GroupType, name: String?): Group = testGroup
        override suspend fun joinGroup(inviteCode: String): Group = testGroup
        override suspend fun getMyGroups(): List<Group> = listOf(testGroup)
        override suspend fun getGroup(groupId: String): Group = testGroup
        override suspend fun deleteGroup(groupId: String) {}
        override suspend fun updateGroupName(groupId: String, name: String): Group = testGroup
    }

    private fun createViewModel() = ProfileViewModel(
        profileRepository = fakeProfileRepository,
        eventRepository = fakeEventRepository,
        groupRepository = fakeGroupRepository,
    )

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        followShouldThrow = false
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ─── loadProfile ───────────────────────────────────────────────────────────

    @Test
    fun `loadProfile 성공 시 Loaded 상태가 된다`() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        viewModel.loadProfile("user-1")

        val state = viewModel.uiState.value
        assertIs<ProfileUiState.Loaded>(state)
        assertEquals("user-1", state.profile.id)
        assertEquals("테스터", state.profile.nickname)
    }

    @Test
    fun `loadProfile 실패 시 Error 상태가 된다`() = runTest(testDispatcher) {
        val failingProfileRepo = object : ProfileRepository by fakeProfileRepository {
            override fun getProfile(userId: String): Flow<UserProfile> =
                kotlinx.coroutines.flow.flow { throw RuntimeException("네트워크 오류") }
        }
        val viewModel = ProfileViewModel(
            failingProfileRepo,
            fakeEventRepository,
            fakeGroupRepository,
        )

        viewModel.loadProfile("user-1")

        assertIs<ProfileUiState.Error>(viewModel.uiState.value)
    }

    // ─── selectSubTab ──────────────────────────────────────────────────────────

    @Test
    fun `selectSubTab으로 Calendar 탭 선택 시 상태가 변경된다`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        viewModel.loadProfile("user-1")

        viewModel.selectSubTab(ProfileSubTab.Calendar)

        val state = viewModel.uiState.value as ProfileUiState.Loaded
        assertEquals(ProfileSubTab.Calendar, state.selectedSubTab)
    }

    // ─── selectCalendarDay ─────────────────────────────────────────────────────

    @Test
    fun `selectCalendarDay 호출 시 selectedCalendarDay가 업데이트된다`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        viewModel.loadProfile("user-1")

        viewModel.selectCalendarDay(15)

        val state = viewModel.uiState.value as ProfileUiState.Loaded
        assertEquals(15, state.selectedCalendarDay)
    }

    @Test
    fun `selectCalendarDay null 호출 시 선택이 해제된다`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        viewModel.loadProfile("user-1")
        viewModel.selectCalendarDay(10)

        viewModel.selectCalendarDay(null)

        val state = viewModel.uiState.value as ProfileUiState.Loaded
        assertEquals(null, state.selectedCalendarDay)
    }

    // ─── navigateMonth ─────────────────────────────────────────────────────────

    @Test
    fun `navigateMonth +1 호출 시 month가 1 증가한다`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        viewModel.loadProfile("user-1")
        val initialMonth = (viewModel.uiState.value as ProfileUiState.Loaded).selectedMonth

        viewModel.navigateMonth(+1)

        val newMonth = (viewModel.uiState.value as ProfileUiState.Loaded).selectedMonth
        assertEquals((initialMonth % 12) + 1, newMonth)
    }

    @Test
    fun `navigateMonth 12월에 +1 하면 1월로 넘어가고 연도가 증가한다`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        viewModel.loadProfile("user-1")
        // 12월로 세팅
        val loaded = viewModel.uiState.value as ProfileUiState.Loaded
        // 직접 12월로 이동하기 위해 필요한 delta 계산
        val delta = 12 - loaded.selectedMonth
        repeat(delta) { viewModel.navigateMonth(+1) }
        val beforeYear = (viewModel.uiState.value as ProfileUiState.Loaded).selectedYear

        viewModel.navigateMonth(+1)

        val state = viewModel.uiState.value as ProfileUiState.Loaded
        assertEquals(1, state.selectedMonth)
        assertEquals(beforeYear + 1, state.selectedYear)
    }

    @Test
    fun `navigateMonth 1월에 -1 하면 12월로 넘어가고 연도가 감소한다`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        viewModel.loadProfile("user-1")
        // 1월로 이동
        val loaded = viewModel.uiState.value as ProfileUiState.Loaded
        val deltaBack = loaded.selectedMonth - 1
        repeat(deltaBack) { viewModel.navigateMonth(-1) }
        val beforeYear = (viewModel.uiState.value as ProfileUiState.Loaded).selectedYear

        viewModel.navigateMonth(-1)

        val state = viewModel.uiState.value as ProfileUiState.Loaded
        assertEquals(12, state.selectedMonth)
        assertEquals(beforeYear - 1, state.selectedYear)
    }

    // ─── toggleFollow ──────────────────────────────────────────────────────────

    @Test
    fun `toggleFollow 호출 시 isFollowing이 즉시 낙관적 업데이트된다`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        viewModel.loadProfile("user-1")
        val loaded = viewModel.uiState.value as ProfileUiState.Loaded
        assertFalse(loaded.profile.isFollowing)

        viewModel.toggleFollow(loaded, "user-1")

        val state = viewModel.uiState.value as ProfileUiState.Loaded
        assertTrue(state.profile.isFollowing)
    }

    @Test
    fun `toggleFollow 실패 시 isFollowing이 롤백된다`() = runTest(testDispatcher) {
        followShouldThrow = true
        val viewModel = createViewModel()
        viewModel.loadProfile("user-1")
        val loaded = viewModel.uiState.value as ProfileUiState.Loaded

        viewModel.toggleFollow(loaded, "user-1")

        val state = viewModel.uiState.value as ProfileUiState.Loaded
        assertFalse(state.profile.isFollowing) // 롤백
    }
}
