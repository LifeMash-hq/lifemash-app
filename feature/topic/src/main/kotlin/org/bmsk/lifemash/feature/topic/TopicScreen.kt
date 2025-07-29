package org.bmsk.lifemash.feature.topic

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.bmsk.lifemash.core.designsystem.component.LifeMashCard
import org.bmsk.lifemash.core.designsystem.component.Loading
import org.bmsk.lifemash.core.designsystem.theme.LifeMashTheme
import org.bmsk.lifemash.core.model.NewsModel
import org.bmsk.lifemash.core.model.section.SBSSection
import java.util.Date

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
    var searchText by remember { mutableStateOf("") }
    var selectedNewsModel by remember { mutableStateOf<NewsModel?>(null) }
    var scrapCompleted by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    TopicScreen(
        isLoading = isLoading,
        newsList = newsList,
        currentSection = currentSection,
        searchText = searchText,
        onSearchTextChange = { searchText = it },
        lazyListState = lazyListState,
        scrapCompleted = scrapCompleted,
        onClickSection = onClickSection,
        onSearchClick = onSearchClick,
        onClickNews = onClickNews,
        selectedNewsModel = selectedNewsModel,
        onClickNewsMore = {
            selectedNewsModel = it
            scrapCompleted = false
        },
        onClickScrap = {
            onClickScrap(it)
            scrapCompleted = true
            coroutineScope.launch {
                delay(1000)
                selectedNewsModel = null
                scrapCompleted = false
            }
        },
        onClickScrapPage = onClickScrapPage,
        onDismissBottomSheet = {
            selectedNewsModel = null
            scrapCompleted = false
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TopicScreen(
    isLoading: Boolean,
    newsList: PersistentList<NewsModel>,
    currentSection: SBSSection,
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    lazyListState: LazyListState,
    scrapCompleted: Boolean,
    onClickSection: (SBSSection) -> Unit,
    onSearchClick: (String) -> Unit,
    onClickNews: (String) -> Unit,
    selectedNewsModel: NewsModel?,
    onClickNewsMore: (NewsModel) -> Unit,
    onClickScrap: (NewsModel) -> Unit,
    onClickScrapPage: () -> Unit,
    onDismissBottomSheet: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        contentWindowInsets = WindowInsets.systemBars,
    ) { paddingValues ->
        Box(
            Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (isLoading) Loading(modifier = Modifier.align(Alignment.Center))

            NewsContent(
                newsList = newsList,
                listState = lazyListState,
                onClickNews = onClickNews,
                onClickNewsMore = onClickNewsMore,
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
                    onClickChip = {
                        coroutineScope.launch { lazyListState.scrollToItem(0) }
                        onClickSection(it)
                    },
                    searchText = searchText,
                    onSearchTextChange = onSearchTextChange,
                    onSearch = onSearchClick,
                    onClickScrapPage = onClickScrapPage,
                )
            }

            if (selectedNewsModel != null) {
                AnimatedVisibility(visible = true) {
                    ModalBottomSheet(
                        onDismissRequest = onDismissBottomSheet,
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                        tonalElevation = 8.dp,
                        scrimColor = MaterialTheme.colorScheme.scrim.copy(alpha = 0.4f),
                    ) {
                        LifeMashCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                        ) {
                            Column(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.feature_topic_scrap),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Spacer(Modifier.height(24.dp))

                                Button(
                                    onClick = { onClickScrap(selectedNewsModel) },
                                    enabled = !scrapCompleted,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (scrapCompleted)
                                            MaterialTheme.colorScheme.secondary
                                        else
                                            MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    )
                                ) {
                                    Text(
                                        text = if (scrapCompleted)
                                            stringResource(R.string.feature_topic_scrap_complete)
                                        else
                                            stringResource(R.string.feature_topic_scrap_do),
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.navigationBarsPadding())
                    }
                }
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
            .padding(horizontal = 16.dp),
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

private data class TopicScreenPreviewState(
    val isLoading: Boolean,
    val newsList: PersistentList<NewsModel>,
    val currentSection: SBSSection,
    val searchText: String = "",
    val selectedNewsModel: NewsModel? = null,
    val scrapCompleted: Boolean = false
)

private class TopicScreenPreviewProvider : PreviewParameterProvider<TopicScreenPreviewState> {
    override val values = sequenceOf(
        TopicScreenPreviewState(
            isLoading = true,
            newsList = persistentListOf(),
            currentSection = SBSSection.ECONOMICS
        ),
        TopicScreenPreviewState(
            isLoading = false,
            newsList = persistentListOf(
                NewsModel(
                    title = "경제 뉴스",
                    link = "https://sbs.co.kr/news/1",
                    pubDate = Date()
                )
            ),
            currentSection = SBSSection.ECONOMICS
        ),
        TopicScreenPreviewState(
            isLoading = false,
            newsList = persistentListOf(
                NewsModel(
                    title = "정치 뉴스",
                    link = "https://sbs.co.kr/news/2",
                    pubDate = Date()
                )
            ),
            currentSection = SBSSection.POLITICS,
            selectedNewsModel = NewsModel(
                title = "정치 뉴스",
                link = "https://sbs.co.kr/news/2",
                pubDate = Date()
            ),
            scrapCompleted = false
        ),
        TopicScreenPreviewState(
            isLoading = false,
            newsList = persistentListOf(
                NewsModel(
                    title = "사회 뉴스",
                    link = "https://sbs.co.kr/news/3",
                    pubDate = Date()
                )
            ),
            currentSection = SBSSection.SOCIAL,
            selectedNewsModel = NewsModel(
                title = "사회 뉴스",
                link = "https://sbs.co.kr/news/3",
                pubDate = Date()
            ),
            scrapCompleted = true
        )
    )
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, name = "Light")
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark")
private fun TopicScreenPreview(
    @PreviewParameter(TopicScreenPreviewProvider::class) state: TopicScreenPreviewState
) {
    LifeMashTheme {
        TopicScreen(
            isLoading = state.isLoading,
            newsList = state.newsList,
            currentSection = state.currentSection,
            searchText = state.searchText,
            onSearchTextChange = {},
            lazyListState = rememberLazyListState(),
            scrapCompleted = state.scrapCompleted,
            onClickSection = {},
            onSearchClick = {},
            onClickNews = {},
            selectedNewsModel = state.selectedNewsModel,
            onClickNewsMore = {},
            onClickScrap = {},
            onClickScrapPage = {},
            onDismissBottomSheet = {}
        )
    }
}