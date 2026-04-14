package org.bmsk.lifemash.feed.impl

import org.bmsk.lifemash.domain.feed.FeedPost

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
