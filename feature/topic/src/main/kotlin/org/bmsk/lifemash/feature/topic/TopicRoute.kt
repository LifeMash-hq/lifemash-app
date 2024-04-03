package org.bmsk.lifemash.feature.topic

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.collections.immutable.PersistentList
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.bmsk.lifemash.core.designsystem.theme.LifeMashTheme
import org.bmsk.lifemash.core.model.NewsModel
import org.bmsk.lifemash.core.model.section.SbsSection

@Composable
internal fun TopicRoute(
    onClickNews: (url: String) -> Unit,
    onShowErrorSnackbar: (throwable: Throwable) -> Unit,
    viewModel: TopicViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(true) { viewModel.errorFlow.collectLatest(onShowErrorSnackbar) }
    TopicScreen(
        newsList = uiState.newsList,
        currentSection = uiState.currentSection,
        onClickSection = viewModel::fetchNews,
        onSearchClick = viewModel::fetchNewsSearchResults,
        onClickNews = onClickNews,
    )
}

@Composable
private fun TopicScreen(
    newsList: PersistentList<NewsModel>,
    currentSection: SbsSection,
    onClickSection: (SbsSection) -> Unit,
    onSearchClick: (String) -> Unit,
    onClickNews: (String) -> Unit,
) {
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    Box(Modifier.fillMaxSize()) {
        NewsContent(
            newsList = newsList,
            listState = lazyListState,
            onClickNews = onClickNews,
        )
        AnimatedVisibility(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            visible = lazyListState.isScrollingUp(),
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            SearchBar(
                currentSection = currentSection,
                onClickChip = { section ->
                    coroutineScope.launch { lazyListState.scrollToItem(0) }
                    onClickSection(section)
                },
                onSearch = onSearchClick,
            )
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
                previousIndex < firstVisibleItemIndex
            } else {
                previousScrollOffset > firstVisibleItemScrollOffset
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
        items(
            items = newsList,
            key = { it.link },
        ) {
            NewsCard(newsModel = it, onClick = { onClickNews(it.link) })
        }
    }
}

fun Modifier.simpleVerticalScrollbar(
    state: LazyListState,
    width: Dp = 4.dp,
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
private fun SearchBar(
    currentSection: SbsSection,
    onClickChip: (SbsSection) -> Unit,
    onSearch: (String) -> Unit,
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
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
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
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
private fun TopicScreenPreview() {
    LifeMashTheme {
    }
}
