package org.bmsk.lifemash.feed.impl

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.bmsk.lifemash.domain.feed.FeedComment
import org.bmsk.lifemash.domain.feed.FeedFilter
import org.bmsk.lifemash.domain.feed.FeedPost
import org.bmsk.lifemash.domain.feed.FeedPage
import org.bmsk.lifemash.domain.feed.FeedRepository
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class FeedViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private val testPost = FeedPost(
        id = "post-1",
        authorId = "user-1",
        authorNickname = "테스터",
        likeCount = 5,
        isLiked = false,
        commentCount = 2,
        createdAt = "2026-04-01",
    )

    private val testComment = FeedComment(
        id = "comment-1",
        authorNickname = "댓글작성자",
        content = "테스트 댓글",
    )

    private var feedResult: FeedPage = FeedPage(items = listOf(testPost))
    private var toggleLikeResult: Boolean = true
    private var commentsResult: List<FeedComment> = listOf(testComment)
    private var toggleLikeShouldThrow = false
    private var lastSearchedFilter: FeedFilter? = null

    private val fakeFeedRepository = object : FeedRepository {
        override suspend fun getFeed(
            filter: FeedFilter,
            cursor: String?,
            limit: Int,
        ): FeedPage {
            lastSearchedFilter = filter
            return feedResult
        }
        override suspend fun toggleLike(postId: String, isCurrentlyLiked: Boolean): Boolean {
            if (toggleLikeShouldThrow) throw RuntimeException("좋아요 실패")
            return toggleLikeResult
        }
        override suspend fun getComments(postId: String): List<FeedComment> = commentsResult
        override suspend fun createComment(postId: String, content: String): FeedComment =
            FeedComment(
                id = "new-comment",
                authorNickname = "나",
                content = content,
            )
    }

    private fun createViewModel() = FeedViewModel(feedRepository = fakeFeedRepository)

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        feedResult = FeedPage(items = listOf(testPost))
        toggleLikeShouldThrow = false
        lastSearchedFilter = null
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ─── loadFeed ──────────────────────────────────────────────────────────────

    @Test
    fun `loadFeed 성공 시 Loaded 상태가 된다`() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        viewModel.loadFeed()

        val state = viewModel.uiState.value
        assertIs<FeedUiState.Loaded>(state)
        assertEquals(1, state.posts.size)
        assertEquals("post-1", state.posts[0].id)
    }

    @Test
    fun `loadFeed 빈 결과이면 Empty 상태가 된다`() = runTest(testDispatcher) {
        feedResult = FeedPage(items = emptyList())
        val viewModel = createViewModel()

        viewModel.loadFeed()

        assertIs<FeedUiState.Empty>(viewModel.uiState.value)
    }

    @Test
    fun `loadFeed 실패 시 Error 상태가 된다`() = runTest(testDispatcher) {
        feedResult = FeedPage(items = emptyList()) // dummy, throw below
        val failingRepo = object : FeedRepository by fakeFeedRepository {
            override suspend fun getFeed(
                filter: FeedFilter,
                cursor: String?,
                limit: Int,
            ): FeedPage =
                throw RuntimeException("네트워크 오류")
        }
        val viewModel = FeedViewModel(feedRepository = failingRepo)

        viewModel.loadFeed()

        val state = viewModel.uiState.value
        assertIs<FeedUiState.Error>(state)
        assertEquals("네트워크 오류", state.message)
    }

    // ─── selectFilter ──────────────────────────────────────────────────────────

    @Test
    fun `selectFilter로 다른 필터 선택 시 피드가 재로드된다`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        viewModel.loadFeed()

        viewModel.selectFilter(FeedFilter.FOLLOWING)

        assertEquals(FeedFilter.FOLLOWING, viewModel.selectedFilter.value)
        assertEquals(FeedFilter.FOLLOWING, lastSearchedFilter)
    }

    @Test
    fun `selectFilter로 동일 필터 선택 시 재로드하지 않는다`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        viewModel.loadFeed()
        lastSearchedFilter = null

        viewModel.selectFilter(FeedFilter.ALL) // 이미 ALL

        assertNull(lastSearchedFilter) // getFeed가 다시 호출되지 않음
    }

    // ─── toggleLike ────────────────────────────────────────────────────────────

    @Test
    fun `toggleLike 호출 시 즉시 낙관적 업데이트된다`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        viewModel.loadFeed()

        viewModel.toggleLike("post-1")

        val state = viewModel.uiState.value as FeedUiState.Loaded
        val post = state.posts.first { it.id == "post-1" }
        assertTrue(post.isLiked)
        assertEquals(6, post.likeCount) // 5 + 1
    }

    @Test
    fun `toggleLike 실패 시 좋아요 상태가 롤백된다`() = runTest(testDispatcher) {
        toggleLikeShouldThrow = true
        val viewModel = createViewModel()
        viewModel.loadFeed()

        viewModel.toggleLike("post-1")

        val state = viewModel.uiState.value as FeedUiState.Loaded
        val post = state.posts.first { it.id == "post-1" }
        assertFalse(post.isLiked) // 원상복구
        assertEquals(5, post.likeCount) // 롤백
    }

    @Test
    fun `이미 좋아요인 post의 toggleLike는 좋아요를 취소한다`() = runTest(testDispatcher) {
        feedResult = FeedPage(items = listOf(testPost.copy(isLiked = true, likeCount = 10)))
        val viewModel = createViewModel()
        viewModel.loadFeed()

        viewModel.toggleLike("post-1")

        val state = viewModel.uiState.value as FeedUiState.Loaded
        val post = state.posts.first { it.id == "post-1" }
        assertFalse(post.isLiked)
        assertEquals(9, post.likeCount) // 10 - 1
    }

    // ─── submitComment ─────────────────────────────────────────────────────────

    @Test
    fun `submitComment 성공 시 comments에 추가되고 commentCount가 증가한다`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        viewModel.loadFeed()
        var doneCalled = false

        viewModel.submitComment("post-1", "새 댓글") { doneCalled = true }

        assertEquals(1, viewModel.comments.value.size)
        assertEquals("새 댓글", viewModel.comments.value[0].content)
        val post = (viewModel.uiState.value as FeedUiState.Loaded).posts.first { it.id == "post-1" }
        assertEquals(3, post.commentCount) // 2 + 1
        assertTrue(doneCalled)
    }

    // ─── loadNextPage ──────────────────────────────────────────────────────────

    @Test
    fun `loadNextPage 호출 시 posts가 누적된다`() = runTest(testDispatcher) {
        feedResult = FeedPage(items = listOf(testPost), nextCursor = "cursor-2")
        val viewModel = createViewModel()
        viewModel.loadFeed()

        val newPost = testPost.copy(id = "post-2")
        feedResult = FeedPage(items = listOf(newPost), nextCursor = null)
        val loaded = viewModel.uiState.value as FeedUiState.Loaded
        viewModel.loadNextPage(loaded)

        val state = viewModel.uiState.value as FeedUiState.Loaded
        assertEquals(2, state.posts.size)
        assertNull(state.nextCursor)
    }

    @Test
    fun `nextCursor가 null이면 loadNextPage가 아무 것도 하지 않는다`() = runTest(testDispatcher) {
        feedResult = FeedPage(items = listOf(testPost), nextCursor = null)
        val viewModel = createViewModel()
        viewModel.loadFeed()
        feedResult = FeedPage(items = listOf(testPost.copy(id = "post-2")))

        val loaded = viewModel.uiState.value as FeedUiState.Loaded
        viewModel.loadNextPage(loaded)

        val state = viewModel.uiState.value as FeedUiState.Loaded
        assertEquals(1, state.posts.size) // 그대로
    }
}
