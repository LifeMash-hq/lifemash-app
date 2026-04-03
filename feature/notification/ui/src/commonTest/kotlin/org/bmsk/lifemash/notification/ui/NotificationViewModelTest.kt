@file:OptIn(kotlin.time.ExperimentalTime::class)
package org.bmsk.lifemash.notification.ui

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.bmsk.lifemash.notification.domain.model.Notification
import org.bmsk.lifemash.notification.domain.model.NotificationType
import org.bmsk.lifemash.notification.domain.repository.NotificationRepository
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class NotificationViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val now: Instant = Clock.System.now()

    private val unreadNotification = Notification(
        id = "1", type = NotificationType.COMMENT,
        actorNickname = "이수아", actorProfileImage = null,
        targetId = null, content = "축하해!",
        isRead = false, createdAt = now,
    )
    private val readNotification = Notification(
        id = "2", type = NotificationType.FOLLOW,
        actorNickname = "정재원", actorProfileImage = null,
        targetId = null, content = null,
        isRead = true, createdAt = now,
    )

    private var returnNotifications: List<Notification> = listOf(unreadNotification, readNotification)
    private var shouldFail = false
    private val markedAsReadIds = mutableListOf<String>()
    private var getNotificationsCallCount = 0

    private val fakeRepository = object : NotificationRepository {
        override suspend fun getNotifications(): List<Notification> {
            getNotificationsCallCount++
            if (shouldFail) throw RuntimeException("네트워크 오류")
            return returnNotifications
        }
        override suspend fun getUnreadCount(): Int = returnNotifications.count { !it.isRead }
        override suspend fun markAsRead(notificationId: String) {
            markedAsReadIds.add(notificationId)
        }
    }

    private fun createViewModel() = NotificationViewModel(fakeRepository)

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        returnNotifications = listOf(unreadNotification, readNotification)
        shouldFail = false
        markedAsReadIds.clear()
        getNotificationsCallCount = 0
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ─── loadNotifications ─────────────────────────────────────────────────────

    @Test
    fun `알림이 있으면 Loaded 상태가 된다`() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        val state = viewModel.uiState.value
        assertIs<NotificationUiState.Loaded>(state)
        assertEquals(2, state.notifications.size)
    }

    @Test
    fun `알림이 비어있으면 Empty 상태가 된다`() = runTest(testDispatcher) {
        returnNotifications = emptyList()
        val viewModel = createViewModel()

        assertEquals(NotificationUiState.Empty, viewModel.uiState.value)
    }

    @Test
    fun `API 오류 시 Error 상태가 된다`() = runTest(testDispatcher) {
        shouldFail = true
        val viewModel = createViewModel()

        val state = viewModel.uiState.value
        assertIs<NotificationUiState.Error>(state)
        assertEquals("네트워크 오류", state.message)
    }

    @Test
    fun `ViewModel 생성 시 init에서 자동으로 로드된다`() = runTest(testDispatcher) {
        createViewModel()

        assertEquals(1, getNotificationsCallCount)
    }

    // ─── markAllAsRead ─────────────────────────────────────────────────────────

    @Test
    fun `markAllAsRead 호출 시 읽지 않은 알림만 처리된다`() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        viewModel.markAllAsRead()

        assertEquals(listOf("1"), markedAsReadIds)
    }

    @Test
    fun `markAllAsRead 모두 읽음 상태이면 API를 호출하지 않는다`() = runTest(testDispatcher) {
        returnNotifications = listOf(readNotification)
        val viewModel = createViewModel()

        viewModel.markAllAsRead()

        assertTrue(markedAsReadIds.isEmpty())
    }

    @Test
    fun `markAllAsRead 후 loadNotifications가 재호출된다`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        val callsBefore = getNotificationsCallCount // 1 (init)

        viewModel.markAllAsRead()

        assertTrue(getNotificationsCallCount > callsBefore)
    }

    @Test
    fun `markAllAsRead Error 상태에서는 아무것도 하지 않는다`() = runTest(testDispatcher) {
        shouldFail = true
        val viewModel = createViewModel()
        assertIs<NotificationUiState.Error>(viewModel.uiState.value)
        val callsBefore = getNotificationsCallCount

        viewModel.markAllAsRead()

        assertEquals(callsBefore, getNotificationsCallCount)
        assertTrue(markedAsReadIds.isEmpty())
    }
}
