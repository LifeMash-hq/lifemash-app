package org.bmsk.lifemash.feature.topic

import io.kotest.core.spec.style.AnnotationSpec
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.bmsk.lifemash.core.model.NewsModel
import org.bmsk.lifemash.core.model.section.SBSSection
import org.bmsk.lifemash.feature.topic.usecase.GetGoogleNewsUseCase
import org.bmsk.lifemash.feature.topic.usecase.GetNewsWithImagesUseCase
import org.bmsk.lifemash.feature.topic.usecase.GetSBSNewsUseCase
import org.bmsk.lifemash.feature.topic.usecase.ScrapNewsUseCase
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Date

class TopicViewModelTest {

    class FakeGetSBSNewsUseCase(
        var newsList: List<NewsModel> = emptyList(),
        var throwError: Throwable? = null
    ) : GetSBSNewsUseCase {
        override suspend fun invoke(section: SBSSection): List<NewsModel> {
            throwError?.let { throw it }
            return newsList
        }
    }

    class FakeGetGoogleNewsUseCase(
        var newsList: List<NewsModel> = emptyList(),
        var throwError: Throwable? = null
    ) : GetGoogleNewsUseCase {
        override suspend fun invoke(query: String): List<NewsModel> {
            throwError?.let { throw it }
            return newsList
        }
    }

    class FakeScrapNewsUseCase(
        var throwError: Throwable? = null
    ) : ScrapNewsUseCase {
        override suspend fun invoke(newsModel: NewsModel) {
            throwError?.let { throw it }
        }
    }

    class FakeGetNewsWithImagesUseCase(
        var imageNewsList: List<NewsModel> = emptyList()
    ) : GetNewsWithImagesUseCase {
        override suspend fun invoke(newsModels: List<NewsModel>): List<NewsModel> = imageNewsList
    }

    private lateinit var viewModel: TopicViewModel
    private lateinit var fakeGetSBSNewsUseCase: FakeGetSBSNewsUseCase
    private lateinit var fakeGetGoogleNewsUseCase: FakeGetGoogleNewsUseCase
    private lateinit var fakeScrapNewsUseCase: FakeScrapNewsUseCase
    private lateinit var fakeGetNewsWithImagesUseCase: FakeGetNewsWithImagesUseCase

    private val testDispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeGetSBSNewsUseCase = FakeGetSBSNewsUseCase()
        fakeGetGoogleNewsUseCase = FakeGetGoogleNewsUseCase()
        fakeScrapNewsUseCase = FakeScrapNewsUseCase()
        fakeGetNewsWithImagesUseCase = FakeGetNewsWithImagesUseCase()
        viewModel = TopicViewModel(
            fakeGetSBSNewsUseCase,
            fakeGetGoogleNewsUseCase,
            fakeScrapNewsUseCase,
            fakeGetNewsWithImagesUseCase
        )
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `setQuery sets query in state`() = runTest {
        // Given
        val query = "soccer"

        // When
        viewModel.setQuery(query)

        // Then
        assertEquals(query, viewModel.uiState.value.query)
    }

    @Test
    fun `getNews 정상 동작`() = runTest {
        // Given
        val section = SBSSection.ECONOMICS
        val pubDate = Date()
        val news = listOf(
            NewsModel(title = "뉴스1", link = "link1", pubDate = pubDate, imageUrl = null)
        )
        fakeGetSBSNewsUseCase.newsList = news
        fakeGetNewsWithImagesUseCase.imageNewsList = news.map { it.copy(imageUrl = "img") }

        // When
        viewModel.getNews(section)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val sectionState = viewModel.uiState.value.sectionStates.first { it.section == section }
        assert(sectionState.newsLoadUiState is NewsLoadUiState.Loaded)
        assertEquals("img", (sectionState.newsLoadUiState as NewsLoadUiState.Loaded).newsModels.first().imageUrl)
    }

    @Test
    fun `getNews 실패시 에러 상태`() = runTest {
        // Given
        val section = SBSSection.POLITICS
        fakeGetSBSNewsUseCase.throwError = RuntimeException("fail")

        // When
        viewModel.getNews(section)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val sectionState = viewModel.uiState.value.sectionStates.first { it.section == section }
        assert(sectionState.newsLoadUiState is NewsLoadUiState.Error)
    }

    @Test
    fun `getGoogleNews 성공시 googleNewsModels 반영`() = runTest {
        // Given
        val pubDate = Date()
        val news = listOf(
            NewsModel(title = "구글뉴스", link = "gLink", pubDate = pubDate, imageUrl = "img2")
        )
        fakeGetGoogleNewsUseCase.newsList = news

        // When
        viewModel.getGoogleNews("query")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(news, viewModel.uiState.value.googleNewsModels)
    }

    @Test
    fun `getGoogleNews 실패시 searchErrorEvent에 예외 저장`() = runTest {
        // Given
        val exception = IllegalStateException("google fail")
        fakeGetGoogleNewsUseCase.throwError = exception

        // When
        viewModel.getGoogleNews("error")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(exception, viewModel.uiState.value.searchErrorEvent)
    }

    @Test
    fun `scrapNews 성공시 ScrapCompleted`() = runTest {
        // Given
        val news = NewsModel(title = "t", link = "l", pubDate = Date(), imageUrl = null)

        // When
        viewModel.scrapNews(news)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(ScrapingUiState.ScrapCompleted, viewModel.uiState.value.scrapingUiState)
    }

    @Test
    fun `scrapNews 실패시 ScrapingError`() = runTest {
        // Given
        val news = NewsModel(title = "t", link = "l", pubDate = Date(), imageUrl = null)
        val ex = Exception("fail")
        fakeScrapNewsUseCase.throwError = ex

        // When
        viewModel.scrapNews(news)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assert(viewModel.uiState.value.scrapingUiState is ScrapingUiState.ScrapingError)
        assertEquals(ex, (viewModel.uiState.value.scrapingUiState as ScrapingUiState.ScrapingError).throwable)
    }
}