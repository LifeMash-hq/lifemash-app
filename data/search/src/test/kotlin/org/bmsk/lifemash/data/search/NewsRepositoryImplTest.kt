package org.bmsk.lifemash.data.search

import kotlinx.coroutines.test.runTest
import org.bmsk.lifemash.data.network.response.NewsItem
import org.bmsk.lifemash.data.network.response.NewsRss
import org.bmsk.lifemash.data.network.response.RssChannel
import org.bmsk.lifemash.data.network.service.GoogleNewsService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class NewsRepositoryImplTest {

    private val fakeGoogleNewsService = object : GoogleNewsService {
        var response: (() -> NewsRss)? = null

        override suspend fun search(query: String): NewsRss {
            return response?.invoke() ?: NewsRss(RssChannel(title = "Google", items = emptyList()))
        }
    }

    private fun createRepository() = NewsRepositoryImpl(
        googleNewsService = fakeGoogleNewsService,
    )

    @Test
    fun `서비스가 아이템을 반환하면 getGoogleNews는 Article 리스트로 매핑한다`() = runTest {
        // Given
        fakeGoogleNewsService.response = {
            NewsRss(
                RssChannel(
                    title = "Google",
                    items = listOf(
                        NewsItem(
                            title = "Google Article",
                            link = "https://news.google.com/1",
                            pubDate = "Tue, 02 Jan 2024 10:00:00 GMT",
                        ),
                    ),
                ),
            )
        }

        // When
        val result = createRepository().getGoogleNews("테스트")

        // Then
        assertEquals(1, result.size)
        assertEquals("Google Article", result[0].title)
        assertEquals("https://news.google.com/1", result[0].link.value)
    }

    @Test
    fun `서비스가 items를 null로 반환하면 getGoogleNews는 빈 리스트를 반환한다`() = runTest {
        // Given
        fakeGoogleNewsService.response = {
            NewsRss(RssChannel(title = "Google", items = null))
        }

        // When
        val result = createRepository().getGoogleNews("테스트")

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `서비스가 예외를 발생시키면 getGoogleNews는 예외를 전파한다`() = runTest {
        // Given
        fakeGoogleNewsService.response = { throw RuntimeException("timeout") }

        // When & Then
        assertThrows<RuntimeException> {
            createRepository().getGoogleNews("테스트")
        }
    }
}
