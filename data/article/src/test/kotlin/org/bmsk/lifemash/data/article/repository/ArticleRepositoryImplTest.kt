package org.bmsk.lifemash.data.article.repository

import kotlinx.coroutines.test.runTest
import org.bmsk.lifemash.core.model.section.LifeMashCategory
import org.bmsk.lifemash.core.network.response.LegacyLifeMashArticleResponse
import org.bmsk.lifemash.core.network.response.LifeMashArticleResponse
import org.bmsk.lifemash.core.network.service.LifeMashFirebaseService
import org.bmsk.lifemash.domain.core.model.ArticleCategory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ArticleRepositoryImplTest {

    private var callCount = 0
    private var lastRequestedCategory: String? = null
    private var responseProvider: (String) -> List<LifeMashArticleResponse> = { emptyList() }

    private val fakeFirebaseService = object : LifeMashFirebaseService {
        override suspend fun getLatestNews(
            limit: Int,
            category: LifeMashCategory?,
        ): List<LegacyLifeMashArticleResponse> = emptyList()

        override suspend fun getArticles(
            category: String,
            limit: Long,
        ): List<LifeMashArticleResponse> {
            callCount++
            lastRequestedCategory = category
            return responseProvider(category)
        }

        override suspend fun searchArticles(
            query: String,
            category: String?,
            limit: Int,
        ): List<LifeMashArticleResponse> = emptyList()
    }

    private lateinit var repository: ArticleRepositoryImpl

    @BeforeEach
    fun setUp() {
        callCount = 0
        lastRequestedCategory = null
        responseProvider = { emptyList() }
        repository = ArticleRepositoryImpl(fakeFirebaseService)
    }

    private fun validResponse(id: String = "1", title: String = "Test") =
        LifeMashArticleResponse(
            id = id,
            publisher = "Publisher",
            title = title,
            summary = "summary",
            link = "https://example.com/$id",
            image = "https://example.com/img.png",
            publishedAt = 1700000000L,
            categories = listOf("politics"),
            visible = true,
        )

    @Test
    fun `서비스가 정상 응답하면 Article 리스트로 매핑된다`() = runTest {
        // Given
        responseProvider = { listOf(validResponse()) }

        // When
        val result = repository.getArticles(ArticleCategory.POLITICS)

        // Then
        assertEquals(1, result.size)
        assertEquals("Test", result[0].title)
        assertEquals(ArticleCategory.POLITICS.key, lastRequestedCategory)
    }

    @Test
    fun `같은 카테고리로 두 번 호출하면 캐시가 적용되어 서비스는 한 번만 호출된다`() = runTest {
        // Given
        responseProvider = { listOf(validResponse()) }
        repository.getArticles(ArticleCategory.POLITICS)

        // When
        val result = repository.getArticles(ArticleCategory.POLITICS)

        // Then
        assertEquals(1, callCount)
        assertEquals(1, result.size)
    }

    @Test
    fun `다른 카테고리로 호출하면 서비스가 각각 호출된다`() = runTest {
        // Given
        responseProvider = { listOf(validResponse()) }
        repository.getArticles(ArticleCategory.POLITICS)

        // When
        repository.getArticles(ArticleCategory.SPORTS)

        // Then
        assertEquals(2, callCount)
    }

    @Test
    fun `변환 실패 항목은 무시되고 나머지만 반환된다`() = runTest {
        // Given
        responseProvider = {
            listOf(
                validResponse(id = "1"),
                LifeMashArticleResponse(id = "2", link = null, publishedAt = null),
                validResponse(id = "3"),
            )
        }

        // When
        val result = repository.getArticles(ArticleCategory.ALL)

        // Then
        assertEquals(2, result.size)
        assertEquals("1", result[0].id.value)
        assertEquals("3", result[1].id.value)
    }

    @Test
    fun `서비스가 빈 리스트를 반환하면 빈 리스트를 반환한다`() = runTest {
        // Given
        responseProvider = { emptyList() }

        // When
        val result = repository.getArticles(ArticleCategory.ECONOMY)

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `서비스가 예외를 발생시키면 호출자에게 전파된다`() = runTest {
        // Given
        responseProvider = { throw RuntimeException("network error") }

        // When & Then
        assertThrows<RuntimeException> {
            repository.getArticles(ArticleCategory.TECH)
        }
    }
}
