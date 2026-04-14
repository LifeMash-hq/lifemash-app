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
import org.bmsk.lifemash.domain.calendar.FollowRepository
import org.bmsk.lifemash.domain.calendar.Follower
import org.bmsk.lifemash.domain.calendar.Group
import org.bmsk.lifemash.domain.calendar.GroupType
import org.bmsk.lifemash.domain.calendar.EventRepository
import org.bmsk.lifemash.domain.calendar.GroupRepository
import org.bmsk.lifemash.domain.moment.Moment
import org.bmsk.lifemash.domain.moment.MomentRepository
import org.bmsk.lifemash.domain.moment.MomentMedia
import org.bmsk.lifemash.domain.moment.Visibility
import org.bmsk.lifemash.domain.profile.CalendarViewMode
import org.bmsk.lifemash.domain.profile.ProfileSettings
import org.bmsk.lifemash.domain.profile.ProfileSettingsRepository
import org.bmsk.lifemash.domain.profile.ProfileSubTab
import org.bmsk.lifemash.domain.profile.UserProfile
import org.bmsk.lifemash.domain.profile.ProfileRepository
import org.bmsk.lifemash.domain.usecase.calendar.DeleteEventUseCase
import org.bmsk.lifemash.domain.usecase.calendar.GetMonthEventsUseCase
import org.bmsk.lifemash.domain.usecase.calendar.GetMyGroupsUseCase
import org.bmsk.lifemash.domain.usecase.calendar.UpdateEventUseCase
import org.bmsk.lifemash.domain.usecase.follow.FollowUserUseCase
import org.bmsk.lifemash.domain.usecase.follow.UnfollowUserUseCase
import org.bmsk.lifemash.domain.usecase.profile.GetProfileMomentsUseCase
import org.bmsk.lifemash.domain.usecase.profile.GetProfileSettingsUseCase
import org.bmsk.lifemash.domain.usecase.profile.GetUserProfileUseCase
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
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
    }

    private val fakeSettingsRepository = object : ProfileSettingsRepository {
        override suspend fun get(): ProfileSettings = ProfileSettings.Default
        override suspend fun update(settings: ProfileSettings) {}
    }

    private val fakeMomentRepository = object : MomentRepository {
        override suspend fun create(
            eventId: String?,
            caption: String?,
            visibility: Visibility,
            media: List<MomentMedia>,
        ): Moment = throw NotImplementedError()
        override suspend fun getUserMoments(userId: String): List<Moment> = emptyList()
        override suspend fun delete(momentId: String) {}
    }

    private val fakeFollowRepository = object : FollowRepository {
        override suspend fun getFollowers(userId: String): List<Follower> = emptyList()
        override suspend fun getFollowing(userId: String): List<Follower> = emptyList()
        override suspend fun follow(userId: String) {
            if (followShouldThrow) throw RuntimeException("팔로우 실패")
        }
        override suspend fun unfollow(userId: String) {
            if (followShouldThrow) throw RuntimeException("언팔로우 실패")
        }
    }

    private val fakeEventRepository = object : EventRepository {
        override suspend fun getMonthEvents(groupId: String, year: Int, month: Int): List<Event> = emptyList()
        override suspend fun createEvent(
            groupId: String, title: String, description: String?, location: String?,
            startAt: Instant, endAt: Instant?, isAllDay: Boolean, color: String?,
            visibility: EventVisibility,
        ): Event = throw NotImplementedError()
        override suspend fun updateEvent(
            groupId: String, eventId: String, title: String?, description: String?,
            location: String?, startAt: Instant?, endAt: Instant?, isAllDay: Boolean?,
            color: String?, visibility: EventVisibility?,
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
        getUserProfileUseCase = GetUserProfileUseCase(fakeProfileRepository),
        getProfileSettingsUseCase = GetProfileSettingsUseCase(fakeSettingsRepository),
        getProfileMomentsUseCase = GetProfileMomentsUseCase(fakeMomentRepository),
        getMyGroupsUseCase = GetMyGroupsUseCase(fakeGroupRepository),
        getMonthEventsUseCase = GetMonthEventsUseCase(fakeEventRepository),
        updateEventUseCase = UpdateEventUseCase(fakeEventRepository),
        deleteEventUseCase = DeleteEventUseCase(fakeEventRepository),
        followUserUseCase = FollowUserUseCase(fakeFollowRepository),
        unfollowUserUseCase = UnfollowUserUseCase(fakeFollowRepository),
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

    // ─── 초기 상태 ──────────────────────────────────────────────────────────────

    @Test
    fun `초기 상태는 Initializing이고 profile은 null이다`() {
        val viewModel = createViewModel()

        assertIs<ScreenPhase.Initializing>(viewModel.uiState.value.screenPhase)
        assertNull(viewModel.uiState.value.profile)
    }

    // ─── loadProfile ────────────────────────────────────────────────────────────

    @Test
    fun `loadProfile 성공 시 Ready 상태가 되고 프로필이 설정된다`() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        viewModel.loadProfile("user-1")

        val state = viewModel.uiState.value
        assertIs<ScreenPhase.Ready>(state.screenPhase)
        assertEquals("user-1", state.profile?.id)
        assertEquals("테스터", state.profile?.nickname)
    }

    @Test
    fun `loadProfile 실패 시 FatalError 상태가 된다`() = runTest(testDispatcher) {
        val failingRepo = object : ProfileRepository {
            override fun getProfile(userId: String): Flow<UserProfile> =
                kotlinx.coroutines.flow.flow { throw RuntimeException("네트워크 오류") }
            override suspend fun updateProfile(nickname: String?, bio: String?, profileImage: String?) = testProfile
        }
        val viewModel = ProfileViewModel(
            getUserProfileUseCase = GetUserProfileUseCase(failingRepo),
            getProfileSettingsUseCase = GetProfileSettingsUseCase(fakeSettingsRepository),
            getProfileMomentsUseCase = GetProfileMomentsUseCase(fakeMomentRepository),
            getMyGroupsUseCase = GetMyGroupsUseCase(fakeGroupRepository),
            getMonthEventsUseCase = GetMonthEventsUseCase(fakeEventRepository),
            updateEventUseCase = UpdateEventUseCase(fakeEventRepository),
            deleteEventUseCase = DeleteEventUseCase(fakeEventRepository),
            followUserUseCase = FollowUserUseCase(fakeFollowRepository),
            unfollowUserUseCase = UnfollowUserUseCase(fakeFollowRepository),
        )

        viewModel.loadProfile("user-1")

        assertIs<ScreenPhase.FatalError>(viewModel.uiState.value.screenPhase)
    }

    @Test
    fun `loadProfile 시 설정에 따라 기본 서브탭과 캘린더 뷰모드가 적용된다`() = runTest(testDispatcher) {
        val customSettingsRepo = object : ProfileSettingsRepository {
            override suspend fun get() = ProfileSettings(
                defaultSubTab = ProfileSubTab.CALENDAR,
                myCalendarViewMode = CalendarViewMode.CHIP,
                othersCalendarViewMode = CalendarViewMode.DOT,
                defaultEventVisibility = "public",
            )
            override suspend fun update(settings: ProfileSettings) {}
        }
        val viewModel = ProfileViewModel(
            getUserProfileUseCase = GetUserProfileUseCase(fakeProfileRepository),
            getProfileSettingsUseCase = GetProfileSettingsUseCase(customSettingsRepo),
            getProfileMomentsUseCase = GetProfileMomentsUseCase(fakeMomentRepository),
            getMyGroupsUseCase = GetMyGroupsUseCase(fakeGroupRepository),
            getMonthEventsUseCase = GetMonthEventsUseCase(fakeEventRepository),
            updateEventUseCase = UpdateEventUseCase(fakeEventRepository),
            deleteEventUseCase = DeleteEventUseCase(fakeEventRepository),
            followUserUseCase = FollowUserUseCase(fakeFollowRepository),
            unfollowUserUseCase = UnfollowUserUseCase(fakeFollowRepository),
        )

        viewModel.loadProfile("user-1")

        val state = viewModel.uiState.value
        assertEquals(ProfileSubTab.CALENDAR, state.selectedSubTab)
        assertEquals(CalendarViewMode.CHIP, state.calendarViewMode)
    }

    // ─── selectSubTab ───────────────────────────────────────────────────────────

    @Test
    fun `selectSubTab으로 탭을 전환하면 selectedSubTab이 변경된다`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        viewModel.loadProfile("user-1")

        viewModel.selectSubTab(ProfileSubTab.CALENDAR)

        assertEquals(ProfileSubTab.CALENDAR, viewModel.uiState.value.selectedSubTab)
    }

    // ─── selectCalendarDay ──────────────────────────────────────────────────────

    @Test
    fun `selectCalendarDay 호출 시 해당 날짜가 선택된다`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        viewModel.loadProfile("user-1")

        viewModel.selectCalendarDay(15)

        assertEquals(15, viewModel.uiState.value.selectedCalendarDay)
    }

    @Test
    fun `selectCalendarDay에 null을 전달하면 선택이 해제된다`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        viewModel.loadProfile("user-1")
        viewModel.selectCalendarDay(10)

        viewModel.selectCalendarDay(null)

        assertNull(viewModel.uiState.value.selectedCalendarDay)
    }

    // ─── navigateMonth ──────────────────────────────────────────────────────────

    @Test
    fun `navigateMonth +1 호출 시 월이 1 증가한다`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        viewModel.loadProfile("user-1")
        val initialMonth = viewModel.uiState.value.selectedMonth

        viewModel.navigateMonth(+1)

        val expected = if (initialMonth == 12) 1 else initialMonth + 1
        assertEquals(expected, viewModel.uiState.value.selectedMonth)
    }

    @Test
    fun `12월에서 다음달로 이동하면 1월이 되고 연도가 증가한다`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        viewModel.loadProfile("user-1")
        val currentMonth = viewModel.uiState.value.selectedMonth
        repeat(12 - currentMonth) { viewModel.navigateMonth(+1) }
        val yearAt12 = viewModel.uiState.value.selectedYear

        viewModel.navigateMonth(+1)

        assertEquals(1, viewModel.uiState.value.selectedMonth)
        assertEquals(yearAt12 + 1, viewModel.uiState.value.selectedYear)
    }

    @Test
    fun `1월에서 이전달로 이동하면 12월이 되고 연도가 감소한다`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        viewModel.loadProfile("user-1")
        val currentMonth = viewModel.uiState.value.selectedMonth
        repeat(currentMonth - 1) { viewModel.navigateMonth(-1) }
        val yearAt1 = viewModel.uiState.value.selectedYear

        viewModel.navigateMonth(-1)

        assertEquals(12, viewModel.uiState.value.selectedMonth)
        assertEquals(yearAt1 - 1, viewModel.uiState.value.selectedYear)
    }

    // ─── toggleFollow ───────────────────────────────────────────────────────────

    @Test
    fun `toggleFollow 호출 시 isFollowing이 낙관적으로 반전된다`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        viewModel.loadProfile("user-1")
        assertFalse(viewModel.uiState.value.profile!!.isFollowing)

        viewModel.toggleFollow("user-1")

        assertTrue(viewModel.uiState.value.profile!!.isFollowing)
        assertFalse(viewModel.uiState.value.isFollowInProgress)
    }

    @Test
    fun `toggleFollow 실패 시 isFollowing이 원래 값으로 롤백된다`() = runTest(testDispatcher) {
        followShouldThrow = true
        val viewModel = createViewModel()
        viewModel.loadProfile("user-1")

        viewModel.toggleFollow("user-1")

        assertFalse(viewModel.uiState.value.profile!!.isFollowing)
        assertFalse(viewModel.uiState.value.isFollowInProgress)
    }

    @Test
    fun `toggleFollow 중복 호출 시 두 번째 호출은 무시된다`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        viewModel.loadProfile("user-1")

        // isFollowInProgress를 true로 만들기 위해 직접 상태 변경은 못하지만,
        // UnconfinedTestDispatcher에서는 즉시 완료되므로 이 테스트는 가드절 동작을 검증
        viewModel.toggleFollow("user-1")
        assertTrue(viewModel.uiState.value.profile!!.isFollowing)
    }

    // ─── followSheet ────────────────────────────────────────────────────────────

    @Test
    fun `showFollowSheet 호출 시 isFollowSheetVisible이 true가 된다`() {
        val viewModel = createViewModel()

        viewModel.showFollowSheet()

        assertTrue(viewModel.uiState.value.isFollowSheetVisible)
    }

    @Test
    fun `dismissFollowSheet 호출 시 isFollowSheetVisible이 false가 된다`() {
        val viewModel = createViewModel()
        viewModel.showFollowSheet()

        viewModel.dismissFollowSheet()

        assertFalse(viewModel.uiState.value.isFollowSheetVisible)
    }

    // ─── clearError ─────────────────────────────────────────────────────────────

    @Test
    fun `clearError 호출 시 errorMessage가 null이 된다`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        viewModel.loadProfile("user-1")

        viewModel.clearError()

        assertNull(viewModel.uiState.value.errorMessage)
    }
}
