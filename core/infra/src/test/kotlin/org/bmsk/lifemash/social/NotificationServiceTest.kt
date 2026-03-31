package org.bmsk.lifemash.social

import org.bmsk.lifemash.fake.FakeNotificationRepository
import kotlin.uuid.Uuid
import kotlin.test.*

class NotificationServiceTest {
    private fun createService(): Pair<NotificationService, FakeNotificationRepository> {
        val repo = FakeNotificationRepository()
        return NotificationService(repo) to repo
    }

    @Test
    fun `팔로우_시_알림이_생성된다`() {
        // Given
        val (service, _) = createService()
        val userA = Uuid.random()
        val userB = Uuid.random()

        // When
        service.createNotification(userB, "follow", actorId = userA, targetId = userA)

        // Then
        val notifications = service.getNotifications(userB)
        assertEquals(1, notifications.size)
        assertEquals("follow", notifications[0].type)
        assertEquals(userA.toString(), notifications[0].actorId)
    }

    @Test
    fun `읽음_처리하면_is_read가_true이다`() {
        // Given
        val (service, _) = createService()
        val userId = Uuid.random()
        service.createNotification(userId, "follow", actorId = Uuid.random(), targetId = null)
        val notifId = Uuid.parse(service.getNotifications(userId).first().id)

        // When
        service.markAsRead(notifId)

        // Then
        assertTrue(service.getNotifications(userId).first().isRead)
    }

    @Test
    fun `unread_count가_정확하다`() {
        // Given
        val (service, _) = createService()
        val userId = Uuid.random()
        repeat(5) { service.createNotification(userId, "follow", actorId = Uuid.random(), targetId = null) }
        val notifications = service.getNotifications(userId)
        service.markAsRead(Uuid.parse(notifications[0].id))
        service.markAsRead(Uuid.parse(notifications[1].id))

        // When
        val count = service.getUnreadCount(userId)

        // Then
        assertEquals(3, count)
    }

    @Test
    fun `100개_초과_시_오래된_것부터_삭제된다`() {
        // Given
        val (service, _) = createService()
        val userId = Uuid.random()
        repeat(105) { service.createNotification(userId, "follow", actorId = Uuid.random(), targetId = null) }

        // When
        val notifications = service.getNotifications(userId)

        // Then
        assertTrue(notifications.size <= 100)
    }
}
