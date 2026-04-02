package org.bmsk.lifemash.feed

import org.bmsk.lifemash.fake.FakeFeedRepository
import org.bmsk.lifemash.model.feed.FeedPostDto
import kotlin.uuid.Uuid
import kotlin.test.*

class FeedServiceTest {
    private fun createService(): Pair<FeedService, FakeFeedRepository> {
        val repo = FakeFeedRepository()
        return FeedService(repo) to repo
    }

    private fun createPost(i: Int) = FeedPostDto(
        id = Uuid.random().toString(), authorId = Uuid.random().toString(),
        authorNickname = "User$i", eventId = Uuid.random().toString(),
        eventTitle = "Event$i", createdAt = "2026-01-01T00:00:${i.toString().padStart(2, '0')}Z",
    )

    @Test
    fun `팔로우한_유저의_순간이_피드에_나온다`() {
        // Given
        val (service, repo) = createService()
        repo.posts.addAll(listOf(createPost(1), createPost(2)))

        // When
        val feed = service.getFeed(Uuid.random(), null)

        // Then
        assertEquals(2, feed.items.size)
    }

    @Test
    fun `피드가_비어있으면_빈_리스트를_반환한다`() {
        // Given
        val (service, _) = createService()

        // When
        val feed = service.getFeed(Uuid.random(), null)

        // Then
        assertTrue(feed.items.isEmpty())
    }

    @Test
    fun `피드는_최신순으로_정렬된다`() {
        // Given
        val (service, repo) = createService()
        repo.posts.addAll((1..3).map { createPost(it) })

        // When
        val feed = service.getFeed(Uuid.random(), null)

        // Then
        assertEquals(3, feed.items.size)
    }

    @Test
    fun `cursor_페이지네이션이_동작한다`() {
        // Given
        val (service, repo) = createService()
        repo.posts.addAll((1..25).map { createPost(it) })

        // When
        val page1 = service.getFeed(Uuid.random(), null, limit = 10)

        // Then
        assertEquals(10, page1.items.size)
        assertNotNull(page1.nextCursor)

        // When - next page
        val page2 = service.getFeed(Uuid.random(), page1.nextCursor, limit = 10)
        assertEquals(10, page2.items.size)
    }
}
