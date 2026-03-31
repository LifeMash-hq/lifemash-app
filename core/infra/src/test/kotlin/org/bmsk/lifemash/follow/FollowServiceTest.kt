package org.bmsk.lifemash.follow

import org.bmsk.lifemash.fake.FakeFollowRepository
import org.bmsk.lifemash.fake.FakeNotificationRepository
import org.bmsk.lifemash.plugins.BadRequestException
import org.bmsk.lifemash.social.NotificationService
import kotlin.uuid.Uuid
import kotlin.test.*

class FollowServiceTest {
    private fun createService(): Triple<FollowService, FakeFollowRepository, FakeNotificationRepository> {
        val followRepo = FakeFollowRepository()
        val notifRepo = FakeNotificationRepository()
        val notifService = NotificationService(notifRepo)
        return Triple(FollowService(followRepo, notifService), followRepo, notifRepo)
    }

    @Test
    fun `팔로우하면_팔로잉_목록에_추가된다`() {
        // Given
        val (service, repo, _) = createService()
        val userA = Uuid.random()
        val userB = Uuid.random()
        repo.addUser(userA, "A")
        repo.addUser(userB, "B")

        // When
        service.follow(userA, userB)

        // Then
        val following = service.getFollowing(userA)
        assertTrue(following.any { it.id == userB.toString() })
    }

    @Test
    fun `언팔로우하면_팔로잉_목록에서_제거된다`() {
        // Given
        val (service, repo, _) = createService()
        val userA = Uuid.random()
        val userB = Uuid.random()
        repo.addUser(userA, "A")
        repo.addUser(userB, "B")
        service.follow(userA, userB)

        // When
        service.unfollow(userA, userB)

        // Then
        val following = service.getFollowing(userA)
        assertTrue(following.none { it.id == userB.toString() })
    }

    @Test
    fun `자기_자신을_팔로우하면_예외가_발생한다`() {
        // Given
        val (service, _, _) = createService()
        val userA = Uuid.random()

        // When & Then
        assertFailsWith<BadRequestException> {
            service.follow(userA, userA)
        }
    }

    @Test
    fun `중복_팔로우는_무시된다`() {
        // Given
        val (service, repo, _) = createService()
        val userA = Uuid.random()
        val userB = Uuid.random()
        repo.addUser(userA, "A")
        repo.addUser(userB, "B")
        service.follow(userA, userB)

        // When
        service.follow(userA, userB)

        // Then
        val following = service.getFollowing(userA)
        assertEquals(1, following.size)
    }

    @Test
    fun `팔로워_목록을_조회할_수_있다`() {
        // Given
        val (service, repo, _) = createService()
        val userA = Uuid.random()
        val userB = Uuid.random()
        val userC = Uuid.random()
        repo.addUser(userA, "A")
        repo.addUser(userB, "B")
        repo.addUser(userC, "C")
        service.follow(userA, userB)
        service.follow(userC, userB)

        // When
        val followers = service.getFollowers(userB)

        // Then
        assertEquals(2, followers.size)
    }

    @Test
    fun `팔로잉_목록을_조회할_수_있다`() {
        // Given
        val (service, repo, _) = createService()
        val userA = Uuid.random()
        val userB = Uuid.random()
        val userC = Uuid.random()
        repo.addUser(userA, "A")
        repo.addUser(userB, "B")
        repo.addUser(userC, "C")
        service.follow(userA, userB)
        service.follow(userA, userC)

        // When
        val following = service.getFollowing(userA)

        // Then
        assertEquals(2, following.size)
    }
}
