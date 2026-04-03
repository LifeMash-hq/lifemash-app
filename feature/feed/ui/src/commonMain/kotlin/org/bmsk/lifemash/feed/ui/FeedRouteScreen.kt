package org.bmsk.lifemash.feed.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun FeedRouteScreen(
    onShowErrorSnackbar: (Throwable?) -> Unit,
    onNavigateToEventDetail: (String) -> Unit = {},
    viewModel: FeedViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()

    var commentPostId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadFeed()
    }

    FeedScreen(
        uiState = uiState,
        selectedFilter = selectedFilter,
        onFilterSelect = viewModel::selectFilter,
        onRetry = viewModel::loadFeed,
        onPostClick = onNavigateToEventDetail,
        onLikeClick = viewModel::toggleLike,
        onCommentClick = { postId -> commentPostId = postId },
    )

    commentPostId?.let { postId ->
        CommentSheet(
            postId = postId,
            viewModel = viewModel,
            onDismiss = { commentPostId = null },
        )
    }
}
