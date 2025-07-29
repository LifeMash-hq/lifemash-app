package org.bmsk.lifemash.feature.topic

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
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
import kotlinx.coroutines.launch
import org.bmsk.lifemash.core.designsystem.component.ErrorBox
import org.bmsk.lifemash.core.designsystem.component.LifeMashCard
import org.bmsk.lifemash.core.designsystem.component.Loading
import org.bmsk.lifemash.core.designsystem.theme.LifeMashTheme
import org.bmsk.lifemash.core.model.NewsModel
import org.bmsk.lifemash.core.model.section.SBSSection
import java.util.Date

@Composable
internal fun TopicScreen(
    uiState: TopicUiState,
    onQueryChange: (String) -> Unit,
    onSectionClick: (SBSSection) -> Unit,
    onSearchClick: (String) -> Unit,
    onNewsClick: (String) -> Unit,
    onScrapNewsClick: (NewsModel) -> Unit,
    onScrapPageClick: () -> Unit,
    onNewsOverflowMenuClick: (NewsModel) -> Unit,
    onDismissSelectedNews: () -> Unit,
) {
    val lazyListState = rememberLazyListState()
    val selectedSection = uiState.selectedSection
    val selectedSectionUiState = uiState.getNewsLoadUiState(selectedSection)

    when (selectedSectionUiState) {
        is NewsLoadUiState.Loading -> {
            TopicLoadingScreen()
        }

        is NewsLoadUiState.Loaded -> {
            TopicLoadedScreen(
                newsList = selectedSectionUiState.newsModels,
                currentSection = selectedSection,
                searchText = uiState.query,
                onSearchTextChange = onQueryChange,
                lazyListState = lazyListState,
                scrapingUiState = uiState.scrapingUiState,
                onClickSection = onSectionClick,
                onSearchClick = onSearchClick,
                onClickNews = onNewsClick,
                selectedNewsModel = uiState.selectedOverflowMenuNews,
                onOverflowMenuClick = onNewsOverflowMenuClick,
                onScrapClick = onScrapNewsClick,
                onClickScrapPage = onScrapPageClick,
                onDismissBottomSheet = onDismissSelectedNews
            )
        }

        is NewsLoadUiState.Error -> {
            ErrorBox(modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable
internal fun TopicLoadingScreen(
    paddingValues: PaddingValues = PaddingValues(),
) {
    Box(
        Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Loading(modifier = Modifier.align(Alignment.Center))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TopicLoadedScreen(
    newsList: List<NewsModel>,
    currentSection: SBSSection,
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    lazyListState: LazyListState,
    scrapingUiState: ScrapingUiState,
    onClickSection: (SBSSection) -> Unit,
    onSearchClick: (String) -> Unit,
    onClickNews: (String) -> Unit,
    selectedNewsModel: NewsModel?,
    onOverflowMenuClick: (NewsModel) -> Unit,
    onScrapClick: (NewsModel) -> Unit,
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
            NewsContent(
                newsList = newsList,
                listState = lazyListState,
                onClickNews = onClickNews,
                onOverflowMenuClick = onOverflowMenuClick,
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
                                    onClick = { onScrapClick(selectedNewsModel) },
                                    enabled = scrapingUiState == ScrapingUiState.Idle,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = when (scrapingUiState) {
                                            is ScrapingUiState.Idle -> MaterialTheme.colorScheme.primary
                                            is ScrapingUiState.IsScraping -> MaterialTheme.colorScheme.primary
                                            is ScrapingUiState.ScrapCompleted -> MaterialTheme.colorScheme.secondary
                                            is ScrapingUiState.ScrapingError -> MaterialTheme.colorScheme.error
                                            else -> error("Unknown state")
                                        },
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    )
                                ) {
                                    Text(
                                        text = when (scrapingUiState) {
                                            is ScrapingUiState.Idle -> stringResource(R.string.feature_topic_scrap_do)
                                            is ScrapingUiState.IsScraping -> stringResource(R.string.feature_topic_scrap_doing)
                                            is ScrapingUiState.ScrapCompleted -> stringResource(R.string.feature_topic_scrap_complete)
                                            is ScrapingUiState.ScrapingError -> stringResource(R.string.feature_topic_scrap_error)
                                            else -> error("Unknown state")
                                        },
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
    onOverflowMenuClick: (NewsModel) -> Unit,
    newsList: List<NewsModel>,
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
                onOverflowMenuClick = { onOverflowMenuClick(item) }
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
        TopicLoadedScreen(
            newsList = state.newsList,
            currentSection = state.currentSection,
            searchText = state.searchText,
            onSearchTextChange = {},
            lazyListState = rememberLazyListState(),
            scrapingUiState = ScrapingUiState.Idle,
            onClickSection = {},
            onSearchClick = {},
            onClickNews = {},
            selectedNewsModel = state.selectedNewsModel,
            onOverflowMenuClick = {},
            onScrapClick = {},
            onClickScrapPage = {},
            onDismissBottomSheet = {}
        )
    }
}