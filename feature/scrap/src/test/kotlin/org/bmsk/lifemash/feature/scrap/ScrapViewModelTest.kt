package org.bmsk.lifemash.feature.scrap

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.bmsk.lifemash.domain.core.model.Article
import org.bmsk.lifemash.domain.core.model.ArticleCategory
import org.bmsk.lifemash.domain.core.model.ArticleId
import org.bmsk.lifemash.domain.core.model.ArticleUrl
import org.bmsk.lifemash.domain.core.model.Publisher
import org.bmsk.lifemash.domain.scrap.usecase.DeleteScrappedArticleUseCase
import org.bmsk.lifemash.domain.scrap.usecase.GetScrappedArticlesUseCase
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant

class ScrapViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeEach
    fun before() {
        Dispatchers.setMain(testDispatcher)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @AfterEach
    fun after() {
        Dispatchers.resetMain()
    }

    @Test
    fun `뉴스가 비어있으면 NewsEmpty`() = runTest {
        // Given: 빈 뉴스 리스트를 반환하는 Fake UseCase 세팅
        val fakeGet = object : GetScrappedArticlesUseCase {
            override suspend fun invoke(): List<Article> = emptyList()
        }
        val fakeDelete = object : DeleteScrappedArticleUseCase {
            override suspend fun invoke(articleId: ArticleId) = Unit
        }
        val viewModel = ScrapViewModel(fakeGet, fakeDelete)

        // When: 뉴스 조회 호출
        viewModel.getScrapNews()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then: UI 상태가 NewsEmpty 인지 검증
        assertTrue(viewModel.uiState.value is ScrapUiState.NewsEmpty)
    }

    @Test
    fun `뉴스가 있으면 NewsLoaded`() = runTest {
        // Given: 뉴스 1개를 반환하는 Fake UseCase 세팅
        val newsList = listOf(
            Article(
                id = ArticleId("1"),
                publisher = Publisher("publisher"),
                title = "title",
                summary = "summary",
                link = ArticleUrl("link"),
                image = null,
                publishedAt = Instant.now(),
                categories = listOf(ArticleCategory.CARTOON)
            )
        )
        val fakeGet = object : GetScrappedArticlesUseCase {
            override suspend fun invoke(): List<Article> = newsList
        }
        val fakeDelete = object : DeleteScrappedArticleUseCase {
            override suspend fun invoke(articleId: ArticleId) = Unit
        }
        val viewModel = ScrapViewModel(fakeGet, fakeDelete)

        // When: 뉴스 조회 호출
        viewModel.getScrapNews()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then: UI 상태가 NewsLoaded이고, 내용이 맞는지 검증
        val uiState = viewModel.uiState.value
        assertTrue(uiState is ScrapUiState.NewsLoaded)
        val loaded = uiState as ScrapUiState.NewsLoaded
        assertTrue(loaded.scraps.size == 1)
        assertEquals("title", loaded.scraps.first().title)
    }

    @Test
    fun `뉴스 조회 중 예외 발생하면 Error`() = runTest {
        // Given: 예외를 발생시키는 Fake UseCase 세팅
        val error = RuntimeException("DB Error")
        val fakeGet = object : GetScrappedArticlesUseCase {
            override suspend fun invoke(): List<Article> = throw error
        }
        val fakeDelete = object : DeleteScrappedArticleUseCase {
            override suspend fun invoke(articleId: ArticleId) = Unit
        }
        val viewModel = ScrapViewModel(fakeGet, fakeDelete)

        // When: 뉴스 조회 호출
        viewModel.getScrapNews()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then: UI 상태가 Error이고, 예외가 맞는지 검증
        val uiState = viewModel.uiState.value
        assertTrue(uiState is ScrapUiState.Error)
        val err = uiState as ScrapUiState.Error
        assertEquals(error, err.throwable)
    }

    @Test
    fun `뉴스 삭제 중 예외 발생하면 Error`() = runTest {
        // Given: 삭제에서 예외를 발생시키는 Fake UseCase 세팅
        val news = ScrapUiModel(
            id = "1",
            title = "title",
            publisher = "라이프매쉬",
            publishedAtRelative = "조금 전",
            link = "link",
            imageUrl = null
        )
        val error = IllegalStateException("삭제 실패")
        val fakeGet = object : GetScrappedArticlesUseCase {
            override suspend fun invoke(): List<Article> = emptyList()
        }
        val fakeDelete = object : DeleteScrappedArticleUseCase {
            override suspend fun invoke(articleId: ArticleId) {
                throw error
            }
        }
        val viewModel = ScrapViewModel(fakeGet, fakeDelete)

        // When: 뉴스 삭제 호출
        viewModel.deleteScrapNews(news)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then: UI 상태가 Error이고, 예외가 맞는지 검증
        val uiState = viewModel.uiState.value
        assertTrue(uiState is ScrapUiState.Error)
        val err = uiState as ScrapUiState.Error
        assertEquals(error, err.throwable)
    }
}