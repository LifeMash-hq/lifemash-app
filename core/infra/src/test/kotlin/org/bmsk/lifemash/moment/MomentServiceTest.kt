package org.bmsk.lifemash.moment

import org.bmsk.lifemash.fake.FakeMomentRepository
import org.bmsk.lifemash.model.moment.CreateMomentRequest
import org.bmsk.lifemash.plugins.ForbiddenException
import kotlin.uuid.Uuid
import kotlin.test.*

class MomentServiceTest {
    private fun createService(): Pair<MomentService, FakeMomentRepository> {
        val repo = FakeMomentRepository()
        return MomentService(repo) to repo
    }

    @Test
    fun `순간을_생성하면_이벤트에_연결된다`() {
        // Given
        val (service, _) = createService()
        val eventId = Uuid.random()

        // When
        val moment = service.create(eventId, Uuid.random(), CreateMomentRequest("img.jpg"))

        // Then
        assertEquals(eventId.toString(), moment.eventId)
    }

    @Test
    fun `유저의_순간_목록을_조회할_수_있다`() {
        // Given
        val (service, _) = createService()
        val userId = Uuid.random()
        repeat(3) { service.create(Uuid.random(), userId, CreateMomentRequest("img$it.jpg")) }

        // When
        val moments = service.findByUser(userId, userId)

        // Then
        assertEquals(3, moments.size)
    }

    @Test
    fun `본인_순간만_삭제할_수_있다`() {
        // Given
        val (service, _) = createService()
        val userA = Uuid.random()
        val userB = Uuid.random()
        val moment = service.create(Uuid.random(), userA, CreateMomentRequest("img.jpg"))

        // When & Then
        assertFailsWith<ForbiddenException> {
            service.delete(Uuid.parse(moment.id), userB)
        }
    }

    @Test
    fun `visibility가_public이면_타인이_볼_수_있다`() {
        // Given
        val (service, _) = createService()
        val author = Uuid.random()
        val viewer = Uuid.random()
        service.create(Uuid.random(), author, CreateMomentRequest("img.jpg", visibility = "public"))

        // When
        val moments = service.findByUser(author, viewer)

        // Then
        assertEquals(1, moments.size)
    }

    @Test
    fun `visibility가_private이면_타인에게_안_보인다`() {
        // Given
        val (service, _) = createService()
        val author = Uuid.random()
        val viewer = Uuid.random()
        service.create(Uuid.random(), author, CreateMomentRequest("img.jpg", visibility = "private"))

        // When
        val moments = service.findByUser(author, viewer)

        // Then
        assertEquals(0, moments.size)
    }
}
