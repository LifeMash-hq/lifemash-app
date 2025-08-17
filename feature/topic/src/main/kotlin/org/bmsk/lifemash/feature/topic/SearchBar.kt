package org.bmsk.lifemash.feature.topic

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import org.bmsk.lifemash.core.designsystem.theme.LifeMashTheme
import org.bmsk.lifemash.core.model.section.LifeMashCategory
import org.bmsk.lifemash.core.model.section.SBSSection
import org.bmsk.lifemash.feature.topic.mapper.stringResource

@Composable
internal fun SearchBar(
    modifier: Modifier = Modifier,
    currentCategory: LifeMashCategory,
    queryText: String,
    onQueryTextChange: (String) -> Unit,
    onChipClick: (LifeMashCategory) -> Unit,
    onSearchClick: (String) -> Unit,
    onScrapPageClick: () -> Unit,
) {
    SearchBar(
        modifier = modifier,
        queryText = queryText,
        onQueryTextChange = onQueryTextChange,
        onSearchClick = onSearchClick,
        onScrapPageClick = onScrapPageClick,
        chipContent = {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                items(
                    items = LifeMashCategory.entries,
                    key = { it.name },
                ) { category ->
                    FilterChip(
                        selected = category == currentCategory,
                        onClick = { onChipClick(category) },
                        label = { Text(text = category.stringResource()) },
                        shape = RoundedCornerShape(50)
                    )
                }
            }
        }
    )
}

@Composable
internal fun SearchBar(
    modifier: Modifier = Modifier,
    currentSection: SBSSection,
    queryText: String,
    onQueryTextChange: (String) -> Unit,
    onChipClick: (SBSSection) -> Unit,
    onSearchClick: (String) -> Unit,
    onScrapPageClick: () -> Unit,
) {
    SearchBar(
        modifier = modifier,
        queryText = queryText,
        onQueryTextChange = onQueryTextChange,
        onSearchClick = onSearchClick,
        onScrapPageClick = onScrapPageClick,
        chipContent = {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                items(
                    items = SectionChip.entries,
                    key = { it.name },
                ) { sectionChip ->
                    FilterChip(
                        selected = sectionChip.section == currentSection,
                        onClick = { onChipClick(sectionChip.section) },
                        label = { Text(text = stringResource(id = sectionChip.chipNameId)) },
                        shape = RoundedCornerShape(50)
                    )
                }
            }
        }
    )
}

@Composable
internal fun SearchBar(
    modifier: Modifier = Modifier,
    queryText: String,
    onQueryTextChange: (String) -> Unit,
    onSearchClick: (String) -> Unit,
    onScrapPageClick: () -> Unit,
    chipContent: @Composable ColumnScope.() -> Unit = {},
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .shadow(2.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            chipContent()

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp),
                    value = queryText,
                    onValueChange = onQueryTextChange,
                    placeholder = { Text(text = stringResource(id = R.string.search_news)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Search,
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            onSearchClick(queryText)
                            keyboardController?.hide()
                        },
                    ),
                )
                Spacer(modifier = Modifier.width(12.dp))
                ScrapStarIcon(
                    modifier = Modifier
                        .size(46.dp)
                        .shadow(2.dp, RoundedCornerShape(12.dp))
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(8.dp),
                    isActive = true,
                    onClick = onScrapPageClick
                )
            }
        }
    }
}

@Composable
private fun ScrapStarIcon(
    modifier: Modifier = Modifier,
    isActive: Boolean = false,
    onClick: () -> Unit = {}
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(46.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = "스크랩",
            tint = if (isActive) MaterialTheme.colorScheme.primary else Color.Gray
        )
    }
}

private sealed interface SearchBarPreviewData {

    data class SbsSearchBarPreviewData(
        val queryText: String,
        val selectedSection: SBSSection,
        val isScrapActive: Boolean
    ) : SearchBarPreviewData

    data class LifeMashSearchBarPreviewData(
        val queryText: String,
        val selectedCategory: LifeMashCategory,
        val isScrapActive: Boolean
    ) : SearchBarPreviewData
}

private class SearchBarPreviewParameterProvider : PreviewParameterProvider<SearchBarPreviewData> {
    override val values: Sequence<SearchBarPreviewData> = sequenceOf(
        SearchBarPreviewData.SbsSearchBarPreviewData(
            queryText = "예시 검색어",
            selectedSection = SBSSection.entries.first(),
            isScrapActive = false
        ),
        SearchBarPreviewData.LifeMashSearchBarPreviewData(
            queryText = "예시 검색어",
            selectedCategory = LifeMashCategory.entries.first(),
            isScrapActive = true
        )
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun SearchBarPreview(
    @PreviewParameter(SearchBarPreviewParameterProvider::class) previewData: SearchBarPreviewData
) {
    LifeMashTheme {
        var queryText by remember { mutableStateOf("") }

        when (previewData) {
            is SearchBarPreviewData.LifeMashSearchBarPreviewData -> {
                SearchBar(
                    modifier = Modifier,
                    currentCategory = previewData.selectedCategory,
                    queryText = queryText,
                    onQueryTextChange = { },
                    onChipClick = { },
                    onSearchClick = {},
                    onScrapPageClick = {}
                )
            }

            is SearchBarPreviewData.SbsSearchBarPreviewData -> {
                SearchBar(
                    modifier = Modifier,
                    currentSection = previewData.selectedSection,
                    queryText = queryText,
                    onQueryTextChange = { queryText = it },
                    onChipClick = {},
                    onSearchClick = {},
                    onScrapPageClick = {}
                )
            }
        }
    }
}