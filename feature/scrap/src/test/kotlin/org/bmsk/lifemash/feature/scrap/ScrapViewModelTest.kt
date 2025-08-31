package org.bmsk.lifemash.feature.scrap

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
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
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class ScrapViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    @DisplayName("Given UseCase가 빈 목록을 반환할 때, When ViewModel이 초기화되면, Then uiState는 초기에 NewsLoading 상태를 가진다")
    fun `Given empty list from UseCase, When ViewModel is initialized, Then uiState is initially NewsLoading`() = runTest {
        // Given
        val getScrappedArticlesUseCase = object : GetScrappedArticlesUseCase {
            override fun invoke() = flowOf(emptyList<Article>())
        }
        val deleteScrappedArticleUseCase = object : DeleteScrappedArticleUseCase {
            override suspend fun invoke(articleId: ArticleId) = Unit
        }

        // When
        val viewModel = ScrapViewModel(getScrappedArticlesUseCase, deleteScrappedArticleUseCase)

        // Then
        assertEquals(ScrapUiState.NewsLoading, viewModel.uiState.value)
    }

    @Test
    @DisplayName("Given UseCase가 빈 목록을 반환할 때, When ViewModel이 초기화되면, Then uiState는 NewsEmpty가 된다")
    fun `Given empty list from UseCase, When ViewModel is initialized, Then uiState becomes NewsEmpty`() = runTest {
        // Given
        val getScrappedArticlesUseCase = object : GetScrappedArticlesUseCase {
            override fun invoke() = flowOf(emptyList<Article>())
        }
        val deleteScrappedArticleUseCase = object : DeleteScrappedArticleUseCase {
            override suspend fun invoke(articleId: ArticleId) = Unit
        }
        val viewModel = ScrapViewModel(getScrappedArticlesUseCase, deleteScrappedArticleUseCase)

        // When
        val emissions = viewModel.uiState.take(2).toList(mutableListOf())
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(ScrapUiState.NewsLoading, emissions[0])
        assertTrue(emissions[1] is ScrapUiState.NewsEmpty)
    }

    @Test
    @DisplayName("Given UseCase가 뉴스 목록을 반환할 때, When ViewModel이 초기화되면, Then uiState는 NewsLoaded가 된다")
    fun `Given a news list from UseCase, When ViewModel is initialized, Then uiState becomes NewsLoaded`() = runTest {
        // Given
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
        val getScrappedArticlesUseCase = object : GetScrappedArticlesUseCase {
            override fun invoke() = flowOf(newsList)
        }
        val deleteScrappedArticleUseCase = object : DeleteScrappedArticleUseCase {
            override suspend fun invoke(articleId: ArticleId) = Unit
        }
        val viewModel = ScrapViewModel(getScrappedArticlesUseCase, deleteScrappedArticleUseCase)

        // When
        val emissions = viewModel.uiState.take(2).toList(mutableListOf())
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(ScrapUiState.NewsLoading, emissions[0])
        val loadedState = emissions[1] as ScrapUiState.NewsLoaded
        assertEquals(1, loadedState.scraps.size)
        assertEquals("title", loadedState.scraps.first().title)
    }

    @Test
    @DisplayName("Given UseCase가 예외를 발생시킬 때, When ViewModel이 초기화되면, Then uiState는 Error가 된다")
    fun `Given an exception from UseCase, When ViewModel is initialized, Then uiState becomes Error`() = runTest {
        // Given
        val exception = RuntimeException("Test exception")
        val getScrappedArticlesUseCase = object : GetScrappedArticlesUseCase {
            override fun invoke() = flow<List<Article>> { throw exception }
        }
        val deleteScrappedArticleUseCase = object : DeleteScrappedArticleUseCase {
            override suspend fun invoke(articleId: ArticleId) = Unit
        }
        val viewModel = ScrapViewModel(getScrappedArticlesUseCase, deleteScrappedArticleUseCase)

        // When
        val emissions = viewModel.uiState.take(2).toList(mutableListOf())
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(ScrapUiState.NewsLoading, emissions[0])
        val errorState = emissions[1] as ScrapUiState.Error
        assertEquals(exception, errorState.throwable)
    }

    @Test
    @DisplayName("Given 특정 뉴스가 있을 때, When 뉴스 삭제를 호출하면, Then deleteScrappedArticleUseCase가 호출된다")
    fun `Given a news, When deleteScrapNews is called, Then deleteScrappedArticleUseCase is called`() = runTest {
        // Given
        val scrap = ScrapUiModel(
            id = "1",
            title = "title",
            publisher = "publisher",
            publishedAtRelative = "1 day ago",
            link = "link",
            imageUrl = null
        )
        val getScrappedArticlesUseCase = object : GetScrappedArticlesUseCase {
            override fun invoke() = flowOf(emptyList<Article>())
        }
        var wasCalled = false
        val deleteScrappedArticleUseCase = object : DeleteScrappedArticleUseCase {
            override suspend fun invoke(articleId: ArticleId) {
                wasCalled = true
            }
        }
        val viewModel = ScrapViewModel(getScrappedArticlesUseCase, deleteScrappedArticleUseCase)

        // When
        viewModel.deleteScrapNews(scrap)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertTrue(wasCalled)
    }
}
