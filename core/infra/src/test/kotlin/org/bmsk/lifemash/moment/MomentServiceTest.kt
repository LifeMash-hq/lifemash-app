package org.bmsk.lifemash.moment

import org.bmsk.lifemash.fake.FakeMomentRepository
import org.bmsk.lifemash.model.moment.CreateMomentRequest
import org.bmsk.lifemash.model.moment.MediaItemDto
import org.bmsk.lifemash.plugins.BadRequestException
import org.bmsk.lifemash.plugins.ForbiddenException
import kotlin.uuid.Uuid
import kotlin.test.*

class MomentServiceTest {
    private fun createService(): Pair<MomentService, FakeMomentRepository> {
        val repo = FakeMomentRepository()
        return MomentService(repo) to repo
    }

    private fun imageMedia(url: String, order: Int = 0) =
        MediaItemDto(mediaUrl = url, mediaType = "image", sortOrder = order)

    // ── 생성 ──

    @Test
    fun `이벤트_태그_없이도_순간을_생성할_수_있다`() {
        val (service, _) = createService()
        val moment = service.create(Uuid.random(), CreateMomentRequest(media = listOf(imageMedia("img.jpg"))))
        assertNull(moment.eventId)
    }

    @Test
    fun `이벤트_태그를_지정하면_eventId가_연결된다`() {
        val (service, _) = createService()
        val eventId = Uuid.random().toString()
        val moment = service.create(Uuid.random(), CreateMomentRequest(eventId = eventId, media = listOf(imageMedia("img.jpg"))))
        assertEquals(eventId, moment.eventId)
    }

    @Test
    fun `캡션만_있어도_순간을_생성할_수_있다`() {
        val (service, _) = createService()
        val moment = service.create(Uuid.random(), CreateMomentRequest(caption = "오늘은 즐거운 하루!"))
        assertEquals("오늘은 즐거운 하루!", moment.caption)
        assertTrue(moment.media.isEmpty())
    }

    @Test
    fun `미디어만_있어도_순간을_생성할_수_있다`() {
        val (service, _) = createService()
        val moment = service.create(Uuid.random(), CreateMomentRequest(media = listOf(imageMedia("img.jpg"))))
        assertEquals(1, moment.media.size)
    }

    @Test
    fun `캡션과_미디어_둘_다_없으면_BadRequestException`() {
        val (service, _) = createService()
        assertFailsWith<BadRequestException> {
            service.create(Uuid.random(), CreateMomentRequest())
        }
    }

    @Test
    fun `미디어_11개_초과_시_BadRequestException`() {
        val (service, _) = createService()
        val media = (0..10).map { imageMedia("img$it.jpg", it) }
        assertFailsWith<BadRequestException> {
            service.create(Uuid.random(), CreateMomentRequest(media = media))
        }
    }

    @Test
    fun `잘못된_visibility_값이면_BadRequestException`() {
        val (service, _) = createService()
        assertFailsWith<BadRequestException> {
            service.create(Uuid.random(), CreateMomentRequest(caption = "test", visibility = "everyone"))
        }
    }

    // ── 조회 ──

    @Test
    fun `유저의_순간_목록을_조회할_수_있다`() {
        val (service, _) = createService()
        val userId = Uuid.random()
        repeat(3) { service.create(userId, CreateMomentRequest(caption = "순간 $it")) }
        assertEquals(3, service.findByUser(userId, userId).size)
    }

    @Test
    fun `public_순간은_타인이_볼_수_있다`() {
        val (service, _) = createService()
        val author = Uuid.random()
        service.create(author, CreateMomentRequest(caption = "공개 순간", visibility = "public"))
        assertEquals(1, service.findByUser(author, Uuid.random()).size)
    }

    @Test
    fun `private_순간은_타인에게_안_보인다`() {
        val (service, _) = createService()
        val author = Uuid.random()
        service.create(author, CreateMomentRequest(caption = "비공개 순간", visibility = "private"))
        assertEquals(0, service.findByUser(author, Uuid.random()).size)
    }

    @Test
    fun `private_순간은_본인_조회_시_보인다`() {
        val (service, _) = createService()
        val author = Uuid.random()
        service.create(author, CreateMomentRequest(caption = "비공개 순간", visibility = "private"))
        assertEquals(1, service.findByUser(author, author).size)
    }

    // ── 삭제 ──

    @Test
    fun `본인_순간만_삭제할_수_있다`() {
        val (service, _) = createService()
        val userA = Uuid.random()
        val userB = Uuid.random()
        val moment = service.create(userA, CreateMomentRequest(caption = "내 순간"))
        assertFailsWith<ForbiddenException> {
            service.delete(Uuid.parse(moment.id), userB)
        }
    }
}
