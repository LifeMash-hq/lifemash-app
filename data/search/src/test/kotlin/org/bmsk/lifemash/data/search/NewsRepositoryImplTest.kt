package org.bmsk.lifemash.data.search

import kotlinx.coroutines.test.runTest
import org.bmsk.lifemash.core.model.section.SBSSection
import org.bmsk.lifemash.core.network.response.NewsItem
import org.bmsk.lifemash.core.network.response.NewsRss
import org.bmsk.lifemash.core.network.response.RssChannel
import org.bmsk.lifemash.core.network.service.GoogleNewsService
import org.bmsk.lifemash.core.network.service.SbsNewsService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class NewsRepositoryImplTest {

    private val fakeSbsNewsService = object : SbsNewsService {
        var response: (() -> NewsRss)? = null

        override suspend fun getNews(sectionId: String, plink: String): NewsRss {
            return response?.invoke() ?: NewsRss(RssChannel(title = "SBS", items = emptyList()))
        }
    }

    private val fakeGoogleNewsService = object : GoogleNewsService {
        var response: (() -> NewsRss)? = null

        override suspend fun search(query: String): NewsRss {
            return response?.invoke() ?: NewsRss(RssChannel(title = "Google", items = emptyList()))
        }
    }

    private fun createRepository() = NewsRepositoryImpl(
        sbsNewsService = fakeSbsNewsService,
        googleNewsService = fakeGoogleNewsService,
    )

    @Test
    @DisplayName("getSbsNews - 서비스가 아이템 리스트를 반환하면 Article 리스트로 매핑된다")
    fun `getSbsNews returns mapped articles when service returns items`() = runTest {
        fakeSbsNewsService.response = {
            NewsRss(
                RssChannel(
                    title = "SBS",
                    items = listOf(
                        NewsItem(
                            title = "Test Title",
                            link = "https://example.com/1",
                            pubDate = "Mon, 01 Jan 2024 12:00:00 GMT",
                        ),
                    ),
                ),
            )
        }

        val result = createRepository().getSbsNews(SBSSection.POLITICS)

        assertEquals(1, result.size)
        assertEquals("Test Title", result[0].title)
        assertEquals("https://example.com/1", result[0].link.value)
    }

    @Test
    @DisplayName("getSbsNews - 서비스가 items=null을 반환하면 빈 리스트를 반환한다")
    fun `getSbsNews returns empty list when items is null`() = runTest {
        fakeSbsNewsService.response = {
            NewsRss(RssChannel(title = "SBS", items = null))
        }

        val result = createRepository().getSbsNews(SBSSection.ECONOMICS)

        assertTrue(result.isEmpty())
    }

    @Test
    @DisplayName("getSbsNews - 서비스가 예외를 발생시키면 호출자에게 전파된다")
    fun `getSbsNews propagates exception from service`() = runTest {
        fakeSbsNewsService.response = { throw RuntimeException("network error") }

        assertThrows<RuntimeException> {
            createRepository().getSbsNews(SBSSection.SOCIAL)
        }
    }

    @Test
    @DisplayName("getGoogleNews - 서비스가 아이템 리스트를 반환하면 Article 리스트로 매핑된다")
    fun `getGoogleNews returns mapped articles when service returns items`() = runTest {
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

        val result = createRepository().getGoogleNews("테스트")

        assertEquals(1, result.size)
        assertEquals("Google Article", result[0].title)
        assertEquals("https://news.google.com/1", result[0].link.value)
    }

    @Test
    @DisplayName("getGoogleNews - 서비스가 items=null을 반환하면 빈 리스트를 반환한다")
    fun `getGoogleNews returns empty list when items is null`() = runTest {
        fakeGoogleNewsService.response = {
            NewsRss(RssChannel(title = "Google", items = null))
        }

        val result = createRepository().getGoogleNews("테스트")

        assertTrue(result.isEmpty())
    }

    @Test
    @DisplayName("getGoogleNews - 서비스가 예외를 발생시키면 호출자에게 전파된다")
    fun `getGoogleNews propagates exception from service`() = runTest {
        fakeGoogleNewsService.response = { throw RuntimeException("timeout") }

        assertThrows<RuntimeException> {
            createRepository().getGoogleNews("테스트")
        }
    }
}
