package org.bmsk.lifemash.feature.feed

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.bmsk.lifemash.core.designsystem.theme.LifeMashTheme
import org.bmsk.lifemash.core.repo.article.api.ArticleCategory

@Composable
internal fun CategoryBar(
    modifier: Modifier = Modifier,
    selectedCategory: ArticleCategory,
    categories: List<ArticleCategory>,
    isSearchMode: Boolean = false,
    queryText: String = "",
    onQueryTextChange: (String) -> Unit = {},
    onQueryTextClear: () -> Unit = {},
    onSearchModeChange: (Boolean) -> Unit = {},
    onSearchClick: (String) -> Unit = {},
    onCategorySelect: (ArticleCategory) -> Unit,
) {
    Surface(
        modifier = modifier,
        tonalElevation = 2.dp,
        shadowElevation = 4.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            // 일반(필터) 모드
            AnimatedVisibility(
                visible = !isSearchMode,
                enter = fadeIn(animationSpec = tween(180)) +
                        slideInHorizontally(animationSpec = tween(220)) { -it / 6 },
                exit = fadeOut(animationSpec = tween(150)) +
                        slideOutHorizontally(animationSpec = tween(220)) { -it / 4 }
            ) {
                FilterModeBar(
                    modifier = Modifier.fillMaxWidth(),
                    selected = selectedCategory,
                    categories = categories,
                    onSearchIconClick = { onSearchModeChange(true) },
                    onSelect = onCategorySelect
                )
            }

            // 검색 모드
            AnimatedVisibility(
                visible = isSearchMode,
                enter = fadeIn(animationSpec = tween(180)) +
                        slideInHorizontally(animationSpec = tween(240)) { it / 6 },
                exit = fadeOut(animationSpec = tween(150)) +
                        slideOutHorizontally(animationSpec = tween(240)) { it / 4 }
            ) {
                SearchModeBar(
                    modifier = Modifier
                        .fillMaxWidth(),
                    // SearchModeBar의 TextField에 아래 focusRequester를 연결해 주세요
                    queryText = queryText,
                    onQueryTextChange = onQueryTextChange,
                    onQueryTextClear = onQueryTextClear,
                    onBackClick = { onSearchModeChange(false) },
                    onSearchClick = { onSearchClick(queryText) },
                )
            }
        }
    }
}

/**
 * 검색 모드 UI
 */
@Composable
private fun SearchModeBar(
    modifier: Modifier = Modifier,
    queryText: String,
    onQueryTextChange: (String) -> Unit,
    onQueryTextClear: () -> Unit,
    onBackClick: () -> Unit,
    onSearchClick: () -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.feature_feed_back)
            )
        }

        TextField(
            modifier = Modifier
                .weight(1f)
                .padding(end = 4.dp),
            value = queryText,
            onValueChange = onQueryTextChange,
            singleLine = true,
            placeholder = {
                Text(stringResource(R.string.feature_feed_search_placeholder))
            },
            trailingIcon = {
                if (queryText.isNotEmpty()) {
                    IconButton(onClick = onQueryTextClear) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = stringResource(R.string.feature_feed_clear)
                        )
                    }
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = { onSearchClick() }
            ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            )
        )

        IconButton(onClick = onSearchClick) {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = stringResource(R.string.feature_feed_search)
            )
        }
    }
}

/**
 * 필터 칩/검색 아이콘 UI
 */
@Composable
private fun FilterModeBar(
    modifier: Modifier = Modifier,
    selected: ArticleCategory,
    categories: List<ArticleCategory>,
    onSearchIconClick: () -> Unit,
    onSelect: (ArticleCategory) -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onSearchIconClick) {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = stringResource(R.string.feature_feed_search)
            )
        }

        FilterChipsRow(
            modifier = Modifier.weight(1f),
            selected = selected,
            categories = categories,
            onSelect = onSelect
        )
    }
}

@Composable
private fun FilterChipsRow(
    modifier: Modifier = Modifier,
    selected: ArticleCategory,
    categories: List<ArticleCategory>,
    onSelect: (ArticleCategory) -> Unit,
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { category ->
            FilterChip(
                selected = selected == category,
                onClick = { onSelect(category) },
                label = { Text(stringResource(category.style.labelRes)) },
                leadingIcon = {
                    Icon(
                        category.style.icon,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            )
        }
    }
}

@Preview
@Composable
private fun CategoryBarPreview_Default() {
    LifeMashTheme {
        var isSearchMode by remember { mutableStateOf(false) }
        CategoryBar(
            selectedCategory = ArticleCategory.ENTERTAINMENT,
            categories = ArticleCategory.entries,
            isSearchMode = isSearchMode,
            onCategorySelect = {},
            onSearchModeChange = { isSearchMode = it }
        )
    }
}

@Preview
@Composable
private fun CategoryBarPreview_Search() {
    LifeMashTheme {
        CategoryBar(
            selectedCategory = ArticleCategory.ENTERTAINMENT,
            categories = ArticleCategory.entries,
            isSearchMode = true,
            queryText = "Compose",
            onCategorySelect = {},
        )
    }
}
