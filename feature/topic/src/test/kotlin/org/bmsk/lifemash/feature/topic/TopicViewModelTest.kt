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
import org.bmsk.lifemash.core.model.NewsModel
import org.bmsk.lifemash.core.model.section.SBSSection
import org.bmsk.lifemash.feature.topic.usecase.GetGoogleNewsUseCase
import org.bmsk.lifemash.feature.topic.usecase.GetSBSNewsUseCase
import org.bmsk.lifemash.feature.topic.usecase.ScrapNewsUseCase
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class TopicViewModelTest {
    private val dispatcher: TestDispatcher = UnconfinedTestDispatcher()

    private val getGoogleNewsUseCase = mockk<GetGoogleNewsUseCase>()
    private val getSBSNewsUseCase = mockk<GetSBSNewsUseCase>()
    private val scrapNewsUseCase = mockk<ScrapNewsUseCase>()

    private lateinit var viewModel: TopicViewModel

    @BeforeEach
    fun beforeEach() {
        Dispatchers.setMain(dispatcher)
        viewModel = TopicViewModel(
            getSBSNewsUseCase = getSBSNewsUseCase,
            getGoogleNewsUseCase = getGoogleNewsUseCase,
            scrapNewsUseCase = scrapNewsUseCase
        )
    }

    @AfterEach
    fun afterEach() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when fetchNews is called, uiState updates with new news list`() = runTest {
        // Given
        val fakeNewsList = listOf(
            NewsModel("1", "Fake News Title 1", Date(), "Fake URL 1"),
            NewsModel("2", "Fake News Title 2", Date(), "Fake URL 2"),
        )

        coEvery { getSBSNewsUseCase(SBSSection.ECONOMICS) } returns fakeNewsList

        // When
        viewModel.fetchNews(SBSSection.ECONOMICS)

        // Then
        assertEquals(SBSSection.ECONOMICS, viewModel.uiState.value.currentSection)
        assertEquals(fakeNewsList, viewModel.uiState.value.newsList)
    }
}
