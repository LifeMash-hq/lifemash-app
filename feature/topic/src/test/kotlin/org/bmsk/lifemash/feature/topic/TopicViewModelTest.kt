package org.bmsk.lifemash.feature.topic

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.bmsk.lifemash.core.domain.usecase.NewsUseCase
import org.bmsk.lifemash.core.model.NewsModel
import org.bmsk.lifemash.core.model.section.SbsSection
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TopicViewModelTest {
    private val dispatcher: TestDispatcher = UnconfinedTestDispatcher()

    private val newsUseCase = mockk<NewsUseCase>()

    private lateinit var viewModel: TopicViewModel

    @BeforeEach
    fun beforeEach() {
        Dispatchers.setMain(dispatcher)
        viewModel = TopicViewModel(newsUseCase)
    }

    @AfterEach
    fun afterEach() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when fetchNews is called, uiState updates with new news list`() = runTest {
        // Given
        val fakeNewsList = listOf(
            NewsModel("1", "Fake News Title 1", "Fake Source 1", "Fake URL 1"),
            NewsModel("2", "Fake News Title 2", "Fake Source 2", "Fake URL 2"),
        )

        coEvery { newsUseCase.getSbsNews(SbsSection.ECONOMICS) } returns fakeNewsList

        // When
        viewModel.fetchNews(SbsSection.ECONOMICS)

        // Then
        assertEquals(SbsSection.ECONOMICS, viewModel.uiState.value.currentSection)
        assertEquals(fakeNewsList, viewModel.uiState.value.newsList)
    }
}
