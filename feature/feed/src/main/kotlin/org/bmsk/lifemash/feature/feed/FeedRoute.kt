package org.bmsk.lifemash.feature.feed

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.bmsk.lifemash.core.repo.article.api.ArticleCategory

internal object FeedRoute {
    const val ROUTE = "feed"

    @Composable
    operator fun invoke(
        viewModel: FeedViewModel = hiltViewModel()
    ) {
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        LaunchedEffect(uiState.selectedCategory) {
            viewModel.getArticles(uiState.selectedCategory)
        }

        FeedScreen(
            selectedCategory = uiState.selectedCategory,
            categories = ArticleCategory.entries,
            articles = uiState.visibleArticles,
            isSearchMode = uiState.isSearchMode,
            queryText = uiState.queryText,
            onArticleOpen = {},
            onQueryTextChange = viewModel::setQueryText,
            onQueryTextClear = { viewModel.setQueryText("") },
            onSearchModeChange = viewModel::setSearchMode,
            onSearchClick = {},
            onCategorySelect = viewModel::setCategory
        )
    }
}