package org.bmsk.lifemash.notification.ui

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.time.Instant
import org.bmsk.lifemash.notification.domain.model.Notification
import org.bmsk.lifemash.notification.domain.model.NotificationType
import org.bmsk.lifemash.notification.domain.repository.NotificationRepository
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class NotificationViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private val sampleNotifications = listOf(
        Notification(
            id = "1",
            type = NotificationType.COMMENT,
            actorNickname = "이수아",
            actorProfileImage = null,
            targetId = null,
            content = "축하해!",
            isRead = false,
            createdAt = Instant.parse("2026-03-30T10:00:00Z"),
        ),
        Notification(
            id = "2",
            type = NotificationType.FOLLOW,
            actorNickname = "정재원",
            actorProfileImage = null,
            targetId = null,
            content = null,
            isRead = true,
            createdAt = Instant.parse("2026-03-29T10:00:00Z"),
        ),
    )

    private var returnNotifications: List<Notification> = sampleNotifications
    private var shouldFail = false
    private val markedAsReadIds = mutableListOf<String>()

    private val fakeRepository = object : NotificationRepository {
        override suspend fun getNotifications(): List<Notification> {
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
        returnNotifications = sampleNotifications
        shouldFail = false
        markedAsReadIds.clear()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `알림이 있으면 Loaded 상태가 된다`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        val state = viewModel.uiState.value
        assertTrue(state is NotificationUiState.Loaded)
        assertEquals(2, (state as NotificationUiState.Loaded).notifications.size)
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
        assertTrue(viewModel.uiState.value is NotificationUiState.Error)
    }

    @Test
    fun `markAllAsRead 호출 시 안 읽은 알림만 처리된다`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        viewModel.markAllAsRead()
        assertEquals(listOf("1"), markedAsReadIds)
    }
}
