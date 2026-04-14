package org.bmsk.lifemash.feed.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.bmsk.lifemash.domain.feed.FeedComment
import org.bmsk.lifemash.domain.feed.FeedFilter
import org.bmsk.lifemash.domain.usecase.feed.CreateFeedCommentUseCase
import org.bmsk.lifemash.domain.usecase.feed.GetFeedCommentsUseCase
import org.bmsk.lifemash.domain.usecase.feed.GetFeedUseCase
import org.bmsk.lifemash.domain.usecase.feed.ToggleFeedLikeUseCase

internal class FeedViewModel(
    private val getFeed: GetFeedUseCase,
    private val toggleFeedLike: ToggleFeedLikeUseCase,
    private val getFeedComments: GetFeedCommentsUseCase,
    private val createFeedComment: CreateFeedCommentUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow<FeedUiState>(FeedUiState.Loading)
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    private val _selectedFilter = MutableStateFlow(FeedFilter.ALL)
    val selectedFilter: StateFlow<FeedFilter> = _selectedFilter.asStateFlow()

    fun loadFeed() {
        viewModelScope.launch {
            _uiState.value = FeedUiState.Loading
            runCatching {
                getFeed(
                    filter = _selectedFilter.value,
                    cursor = null,
                    limit = 20,
                )
            }.onSuccess { page ->
                _uiState.value = if (page.items.isEmpty()) FeedUiState.Empty
                else FeedUiState.Loaded(posts = page.items, nextCursor = page.nextCursor)
            }.onFailure {
                _uiState.value = FeedUiState.Error(it.message ?: "오류")
            }
        }
    }

    fun selectFilter(filter: FeedFilter) {
        if (filter == _selectedFilter.value) return
        _selectedFilter.value = filter
        loadFeed()
    }

    fun refresh(loaded: FeedUiState.Loaded) {
        _uiState.value = loaded.copy(isRefreshing = true)
        loadFeed()
    }

    fun toggleLike(postId: String) {
        val current = _uiState.value as? FeedUiState.Loaded ?: return
        val post = current.posts.find { it.id == postId } ?: return
        val wasLiked = post.isLiked
        _uiState.update { state ->
            if (state is FeedUiState.Loaded) {
                state.copy(posts = state.posts.map { p ->
                    if (p.id == postId) p.copy(
                        isLiked = !wasLiked,
                        likeCount = if (wasLiked) p.likeCount - 1 else p.likeCount + 1,
                    ) else p
                })
            } else state
        }
        viewModelScope.launch {
            runCatching {
                toggleFeedLike(postId, wasLiked)
            }.onFailure {
                _uiState.update { state ->
                    if (state is FeedUiState.Loaded) {
                        state.copy(posts = state.posts.map { p ->
                            if (p.id == postId) p.copy(
                                isLiked = wasLiked,
                                likeCount = if (wasLiked) p.likeCount + 1 else p.likeCount - 1,
                            ) else p
                        })
                    } else state
                }
            }
        }
    }

    // ─── 댓글 ───────────────────────────────────────────
    private val _comments = MutableStateFlow<List<FeedComment>>(emptyList())
    val comments: StateFlow<List<FeedComment>> = _comments.asStateFlow()

    fun loadComments(postId: String) {
        viewModelScope.launch {
            runCatching { getFeedComments(postId) }
                .onSuccess { _comments.value = it }
        }
    }

    fun submitComment(postId: String, content: String) {
        viewModelScope.launch {
            runCatching { createFeedComment(postId, content) }
                .onSuccess { comment ->
                    _comments.update { it + comment }
                    _uiState.update { state ->
                        if (state is FeedUiState.Loaded) {
                            state.copy(posts = state.posts.map { p ->
                                if (p.id == postId) p.copy(commentCount = p.commentCount + 1) else p
                            })
                        } else state
                    }
                }
        }
    }

    fun clearComments() {
        _comments.value = emptyList()
    }

    fun loadNextPage(loaded: FeedUiState.Loaded) {
        val cursor = loaded.nextCursor ?: return
        viewModelScope.launch {
            runCatching {
                getFeed(
                    filter = _selectedFilter.value,
                    cursor = cursor,
                    limit = 20,
                )
            }.onSuccess { page ->
                _uiState.update { state ->
                    if (state is FeedUiState.Loaded)
                        state.copy(posts = state.posts + page.items, nextCursor = page.nextCursor)
                    else state
                }
            }.onFailure { e -> e.printStackTrace() }
        }
    }
}
