package org.bmsk.lifemash.like

import org.bmsk.lifemash.fake.FakeLikeRepository
import kotlin.uuid.Uuid
import kotlin.test.*

class LikeServiceTest {
    private fun createService(): Pair<LikeService, FakeLikeRepository> {
        val repo = FakeLikeRepository()
        return LikeService(repo) to repo
    }

    @Test
    fun `좋아요를_누르면_카운트가_증가한다`() {
        // Given
        val (service, _) = createService()
        val momentId = Uuid.random()

        // When
        service.like(Uuid.random(), momentId)

        // Then
        assertEquals(1, service.getLikeCount(momentId))
    }

    @Test
    fun `좋아요를_취소하면_카운트가_감소한다`() {
        // Given
        val (service, _) = createService()
        val userId = Uuid.random()
        val momentId = Uuid.random()
        service.like(userId, momentId)

        // When
        service.unlike(userId, momentId)

        // Then
        assertEquals(0, service.getLikeCount(momentId))
    }

    @Test
    fun `중복_좋아요는_무시된다`() {
        // Given
        val (service, _) = createService()
        val userId = Uuid.random()
        val momentId = Uuid.random()
        service.like(userId, momentId)

        // When
        service.like(userId, momentId)

        // Then
        assertEquals(1, service.getLikeCount(momentId))
    }
}
