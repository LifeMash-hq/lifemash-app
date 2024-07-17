package org.bmsk.lifemash.feature.topic

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch
import org.bmsk.lifemash.core.designsystem.component.Loading
import org.bmsk.lifemash.core.designsystem.theme.LifeMashTheme
import org.bmsk.lifemash.core.model.NewsModel
import org.bmsk.lifemash.core.model.section.SBSSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TopicScreen(
    isLoading: Boolean,
    newsList: PersistentList<NewsModel>,
    currentSection: SBSSection,
    onClickSection: (SBSSection) -> Unit,
    onSearchClick: (String) -> Unit,
    onClickNews: (String) -> Unit,
    onClickScrap: (NewsModel) -> Unit,
    onClickScrapPage: () -> Unit,
) {
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var bottomSheetState by remember { mutableStateOf<NewsModel?>(null) }

    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (isLoading) Loading(modifier = Modifier.align(Alignment.Center))
        NewsContent(
            newsList = newsList,
            listState = lazyListState,
            onClickNews = onClickNews,
            onClickNewsMore = { newsModel -> bottomSheetState = newsModel },
        )
        AnimatedVisibility(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            visible = newsList.isEmpty() || lazyListState.isScrollingUp(),
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            SearchBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                currentSection = currentSection,
                onClickChip = { section ->
                    coroutineScope.launch { lazyListState.scrollToItem(0) }
                    onClickSection(section)
                },
                onSearch = onSearchClick,
                onClickScrapPage = onClickScrapPage,
            )
        }

        AnimatedVisibility(
            visible = bottomSheetState != null
        ) {
            ModalBottomSheet(
                onDismissRequest = { bottomSheetState = null },
            ) {
                val newsModel = bottomSheetState ?: return@ModalBottomSheet

                Button(
                    onClick = { onClickScrap(newsModel) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = "스크랩",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(Modifier.navigationBarsPadding())
            }
        }
    }
}

@Composable
private fun LazyListState.isScrollingUp(): Boolean {
    var previousIndex by remember(this) { mutableIntStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by remember(this) { mutableIntStateOf(firstVisibleItemScrollOffset) }
    return remember {
        derivedStateOf {
            if (previousIndex != firstVisibleItemIndex) {
                previousIndex > firstVisibleItemIndex
            } else {
                previousScrollOffset >= firstVisibleItemScrollOffset
            }.also {
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
            }
        }
    }.value
}

@Composable
private fun NewsContent(
    onClickNews: (String) -> Unit,
    onClickNewsMore: (NewsModel) -> Unit,
    newsList: PersistentList<NewsModel>,
    listState: LazyListState,
) {
    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .simpleVerticalScrollbar(listState),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        itemsIndexed(
            items = newsList,
            key = { _, item -> item.link },
        ) { index, item ->
            NewsCard(
                modifier = Modifier
                    .wrapContentHeight()
                    .graphicsLayer {
                        if (index == listState.firstVisibleItemIndex) {
                            alpha = .9f - listState.firstVisibleItemScrollOffset / size.height
                        }
                    },
                newsModel = item,
                onClick = { onClickNews(item.link) },
                onClickMore = { onClickNewsMore(item) }
            )
        }
    }
}

fun Modifier.simpleVerticalScrollbar(
    state: LazyListState,
    width: Dp = 2.dp,
): Modifier = composed {
    val targetAlpha = if (state.isScrollInProgress) 1f else 0f
    val duration = if (state.isScrollInProgress) 150 else 500

    val alpha by animateFloatAsState(
        targetValue = targetAlpha,
        animationSpec = tween(durationMillis = duration),
        label = "",
    )

    drawWithContent {
        drawContent()

        val firstVisibleElementIndex =
            state.layoutInfo.visibleItemsInfo.firstOrNull()?.index ?: return@drawWithContent
        val needDrawScrollbar = state.isScrollInProgress || alpha > 0.0f

        if (needDrawScrollbar) {
            val elementHeight = this.size.height / state.layoutInfo.totalItemsCount
            val scrollbarOffsetY = firstVisibleElementIndex * elementHeight
            val scrollbarHeight = state.layoutInfo.visibleItemsInfo.size * elementHeight

            drawOval(
                color = Color.Cyan.copy(alpha = 0.5f),
                topLeft = Offset(this.size.width - width.toPx(), scrollbarOffsetY),
                size = Size(width.toPx(), scrollbarHeight),
                alpha = alpha,
            )
        }
    }
}

@Composable
private fun SearchBar(
    currentSection: SBSSection,
    onClickChip: (SBSSection) -> Unit,
    onSearch: (String) -> Unit,
    onClickScrapPage: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var searchText by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    Surface(modifier = modifier) {
        Column {
            LazyRow(
                modifier = Modifier,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                items(
                    items = SectionChip.entries,
                    key = { it.name },
                ) { sectionChip ->
                    FilterChip(
                        selected = sectionChip.section == currentSection,
                        onClick = { onClickChip(sectionChip.section) },
                        label = { Text(text = stringResource(id = sectionChip.chipNameId)) },
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = searchText,
                    onValueChange = { searchText = it },
                    placeholder = { Text(text = stringResource(id = R.string.search_news)) },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Search,
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            onSearch(searchText)
                            keyboardController?.hide()
                        },
                    ),
                )
                Icon(
                    imageVector = Icons.Filled.Star,
                    modifier = Modifier
                        .size(50.dp)
                        .clickable { onClickScrapPage() },
                    contentDescription = "스크랩",
                    tint = MaterialTheme.colorScheme.inversePrimary
                )
            }
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
private fun TopicScreenPreview() {
    LifeMashTheme {
        TopicScreen(
            isLoading = true,
            newsList = persistentListOf(),
            currentSection = SBSSection.ECONOMICS,
            onClickSection = {},
            onSearchClick = {},
            onClickNews = {},
            onClickScrap = {},
            onClickScrapPage = {},
        )
    }
}
