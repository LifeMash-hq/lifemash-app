package org.bmsk.lifemash.feed.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.bmsk.lifemash.feed.domain.model.FeedPost
import org.bmsk.lifemash.feed.domain.usecase.GetFeedUseCase

sealed interface FeedUiState {
    data object Loading : FeedUiState
    data class Loaded(
        val posts: List<FeedPost>,
        val followingCount: Int = 0,
        val nextCursor: String? = null,
        val isRefreshing: Boolean = false,
    ) : FeedUiState
    data object Empty : FeedUiState
    data class Error(val message: String) : FeedUiState
}

class FeedViewModel(
    private val getFeedUseCase: GetFeedUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow<FeedUiState>(FeedUiState.Loading)
    val uiState: StateFlow<FeedUiState> = _uiState

    fun loadFeed() {
        viewModelScope.launch {
            getFeedUseCase()
                .catch { _uiState.value = FeedUiState.Error(it.message ?: "오류") }
                .collect { page ->
                    _uiState.value = if (page.items.isEmpty()) FeedUiState.Empty
                    else FeedUiState.Loaded(posts = page.items, nextCursor = page.nextCursor)
                }
        }
    }

    fun refresh(loaded: FeedUiState.Loaded) {
        _uiState.value = loaded.copy(isRefreshing = true)
        loadFeed()
    }

    fun loadNextPage(loaded: FeedUiState.Loaded) {
        val cursor = loaded.nextCursor ?: return
        viewModelScope.launch {
            getFeedUseCase(cursor)
                .catch { /* ignore */ }
                .collect { page ->
                    _uiState.update { state ->
                        if (state is FeedUiState.Loaded)
                            state.copy(posts = state.posts + page.items, nextCursor = page.nextCursor)
                        else state
                    }
                }
        }
    }
}
