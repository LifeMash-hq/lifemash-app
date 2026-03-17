package org.bmsk.lifemash.feed.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.runtime.collectAsState
import org.bmsk.lifemash.model.ArticleCategory

@Composable
internal fun FeedRouteScreen(
    onArticleOpen: (String) -> Unit,
    onNotificationClick: () -> Unit,
    viewModel: FeedViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.selectedCategory) {
        viewModel.getArticles(uiState.selectedCategory)
    }

    val visibleCategories = if (uiState.subscribedCategories.isEmpty()) {
        ArticleCategory.entries
    } else {
        listOf(ArticleCategory.ALL) + uiState.subscribedCategories.sortedBy { it.ordinal }
    }

    FeedScreen(
        onNotificationClick = onNotificationClick,
        selectedCategory = uiState.selectedCategory,
        categories = visibleCategories,
        articles = uiState.visibleArticles,
        isSearchMode = uiState.isSearchMode,
        queryText = uiState.queryText,
        onArticleOpen = {
            viewModel.addToHistory(it.article)
            onArticleOpen(it.article.link.value)
        },
        onScrapClick = viewModel::scrapArticle,
        onQueryTextChange = viewModel::setQueryText,
        onQueryTextClear = { viewModel.setQueryText("") },
        onSearchModeChange = viewModel::setSearchMode,
        onSearchClick = viewModel::searchArticles,
        onCategorySelect = viewModel::setCategory,
        subscribedCategories = uiState.subscribedCategories,
        onSetSubscribedCategories = viewModel::setSubscribedCategories,
    )
}
