package org.bmsk.lifemash.notification.ui

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.time.Clock
import org.bmsk.lifemash.notification.domain.model.Keyword
import org.bmsk.lifemash.notification.domain.model.NotificationKeyword
import org.bmsk.lifemash.notification.domain.repository.KeywordRepository
import org.bmsk.lifemash.notification.domain.usecase.AddKeywordUseCase
import org.bmsk.lifemash.notification.domain.usecase.GetKeywordsUseCase
import org.bmsk.lifemash.notification.domain.usecase.RemoveKeywordUseCase
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class NotificationViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private val fakeKeywords = MutableStateFlow<List<NotificationKeyword>>(emptyList())
    private var addedKeyword: Keyword? = null
    private var removedId: Long? = null

    private val fakeRepository = object : KeywordRepository {
        override fun getKeywords(): Flow<List<NotificationKeyword>> = fakeKeywords
        override suspend fun addKeyword(keyword: Keyword) { addedKeyword = keyword }
        override suspend fun removeKeyword(id: Long) { removedId = id }
    }

    private fun createViewModel() = NotificationViewModel(
        getKeywordsUseCase = GetKeywordsUseCase(fakeRepository),
        addKeywordUseCase = AddKeywordUseCase(fakeRepository),
        removeKeywordUseCase = RemoveKeywordUseCase(fakeRepository),
    )

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        addedKeyword = null
        removedId = null
        fakeKeywords.value = emptyList()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `키워드가 비어있으면 Empty 상태가 된다`() = runTest(testDispatcher) {
        fakeKeywords.value = emptyList()
        val viewModel = createViewModel()

        val values = mutableListOf<NotificationUiState>()
        val job = backgroundScope.launch(testDispatcher) {
            viewModel.uiState.collect { values.add(it) }
        }

        assertTrue(values.any { it == NotificationUiState.Empty })
        job.cancel()
    }

    @Test
    fun `키워드가 있으면 Loaded 상태가 된다`() = runTest(testDispatcher) {
        fakeKeywords.value = listOf(
            NotificationKeyword(id = 1, keyword = Keyword("삼성"), createdAt = Clock.System.now()),
        )
        val viewModel = createViewModel()

        val values = mutableListOf<NotificationUiState>()
        val job = backgroundScope.launch(testDispatcher) {
            viewModel.uiState.collect { values.add(it) }
        }

        val loaded = values.filterIsInstance<NotificationUiState.Loaded>()
        assertTrue(loaded.isNotEmpty())
        assertEquals("삼성", loaded.last().keywords[0].keyword.value)
        job.cancel()
    }

    @Test
    fun `addKeyword 호출 시 UseCase에 정규화되어 전달된다`() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        viewModel.addKeyword("  반도체  ")

        assertEquals("반도체", addedKeyword?.value)
    }

    @Test
    fun `빈 키워드는 무시된다`() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        viewModel.addKeyword("   ")

        assertNull(addedKeyword)
    }

    @Test
    fun `removeKeyword 호출 시 UseCase에 전달된다`() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        viewModel.removeKeyword(42)

        assertEquals(42L, removedId)
    }

    @Test
    fun `키워드 목록 변경 시 UI 상태가 업데이트된다`() = runTest(testDispatcher) {
        fakeKeywords.value = emptyList()
        val viewModel = createViewModel()

        val values = mutableListOf<NotificationUiState>()
        val job = backgroundScope.launch(testDispatcher) {
            viewModel.uiState.collect { values.add(it) }
        }
        assertTrue(values.any { it == NotificationUiState.Empty })

        fakeKeywords.value = listOf(
            NotificationKeyword(id = 1, keyword = Keyword("AI"), createdAt = Clock.System.now()),
            NotificationKeyword(id = 2, keyword = Keyword("반도체"), createdAt = Clock.System.now()),
        )

        val loaded = values.filterIsInstance<NotificationUiState.Loaded>()
        assertTrue(loaded.isNotEmpty())
        assertEquals(2, loaded.last().keywords.size)
        job.cancel()
    }
}
