package org.bmsk.lifemash.feed.ui

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
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import org.bmsk.lifemash.model.ArticleCategory

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
    onSubscriptionSettingClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
) {
    Surface(
        modifier = modifier,
        tonalElevation = 2.dp,
        shadowElevation = 4.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            AnimatedVisibility(
                visible = !isSearchMode,
                enter = fadeIn(animationSpec = tween(180)) + slideInHorizontally(animationSpec = tween(220)) { -it / 6 },
                exit = fadeOut(animationSpec = tween(150)) + slideOutHorizontally(animationSpec = tween(220)) { -it / 4 }
            ) {
                FilterModeBar(
                    modifier = Modifier.fillMaxWidth(),
                    selected = selectedCategory,
                    categories = categories,
                    onSearchIconClick = { onSearchModeChange(true) },
                    onSelect = onCategorySelect,
                    onSubscriptionSettingClick = onSubscriptionSettingClick,
                    onNotificationClick = onNotificationClick,
                )
            }

            AnimatedVisibility(
                visible = isSearchMode,
                enter = fadeIn(animationSpec = tween(180)) + slideInHorizontally(animationSpec = tween(240)) { it / 6 },
                exit = fadeOut(animationSpec = tween(150)) + slideOutHorizontally(animationSpec = tween(240)) { it / 4 }
            ) {
                SearchModeBar(
                    modifier = Modifier.fillMaxWidth(),
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

@Composable
private fun SearchModeBar(
    modifier: Modifier = Modifier,
    queryText: String,
    onQueryTextChange: (String) -> Unit,
    onQueryTextClear: () -> Unit,
    onBackClick: () -> Unit,
    onSearchClick: () -> Unit,
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onBackClick) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로")
        }
        TextField(
            modifier = Modifier.weight(1f).padding(end = 4.dp),
            value = queryText,
            onValueChange = onQueryTextChange,
            singleLine = true,
            placeholder = { Text("검색어를 입력하세요") },
            trailingIcon = {
                if (queryText.isNotEmpty()) {
                    IconButton(onClick = onQueryTextClear) {
                        Icon(imageVector = Icons.Filled.Close, contentDescription = "지우기")
                    }
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearchClick() }),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            )
        )
        IconButton(onClick = onSearchClick) {
            Icon(imageVector = Icons.Filled.Search, contentDescription = "검색")
        }
    }
}

@Composable
private fun FilterModeBar(
    modifier: Modifier = Modifier,
    selected: ArticleCategory,
    categories: List<ArticleCategory>,
    onSearchIconClick: () -> Unit,
    onSelect: (ArticleCategory) -> Unit,
    onSubscriptionSettingClick: () -> Unit,
    onNotificationClick: () -> Unit,
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onSearchIconClick) {
            Icon(imageVector = Icons.Filled.Search, contentDescription = "검색")
        }
        FilterChipsRow(modifier = Modifier.weight(1f), selected = selected, categories = categories, onSelect = onSelect)
        IconButton(onClick = onNotificationClick) {
            Icon(imageVector = Icons.Outlined.Notifications, contentDescription = "키워드 알림")
        }
        IconButton(onClick = onSubscriptionSettingClick) {
            Icon(imageVector = Icons.Outlined.Tune, contentDescription = "구독 설정")
        }
    }
}

@Composable
private fun FilterChipsRow(
    modifier: Modifier = Modifier,
    selected: ArticleCategory,
    categories: List<ArticleCategory>,
    onSelect: (ArticleCategory) -> Unit,
) {
    LazyRow(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(categories) { category ->
            FilterChip(
                selected = selected == category,
                onClick = { onSelect(category) },
                label = { Text(category.style.label) },
                leadingIcon = {
                    Icon(category.style.icon, contentDescription = category.style.label, modifier = Modifier.size(16.dp))
                }
            )
        }
    }
}
