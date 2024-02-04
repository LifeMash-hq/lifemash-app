package org.bmsk.feature.topic

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import org.bmsk.core.designsystem.theme.LifeMashTheme
import org.bmsk.core.model.NewsModel
import org.bmsk.core.model.section.SbsSection
import org.bmsk.lifemash.feature.topic.R

@Composable
internal fun TopicRoute(
    onClickNews: (url: String) -> Unit,
    viewModel: TopicViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    TopicScreen(
        newsList = uiState.newsList,
        currentSection = uiState.currentSection,
        onClickSection = viewModel::fetchNews,
        onSearchClick = viewModel::fetchNewsSearchResults,
        onClickNews = onClickNews,
    )
}

@Composable
private fun NotFoundAnimation() {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.search_not_found))
    LottieAnimation(composition, iterations = LottieConstants.IterateForever)
}

@Composable
private fun TopicScreen(
    newsList: List<NewsModel>,
    currentSection: SbsSection,
    onClickSection: (SbsSection) -> Unit,
    onSearchClick: (String) -> Unit,
    onClickNews: (String) -> Unit,
) {
    val lazyListState = rememberLazyListState()
    var searchBarVisible by remember { mutableStateOf(true) }

    // 스크롤 오프셋 변화 감지
    LaunchedEffect(key1 = Unit) {
        snapshotFlow { lazyListState.firstVisibleItemScrollOffset }
            .map { offset -> offset > 0 } // 스크롤 오프셋이 0보다 크면 스크롤이 아래로 내려갔다는 의미
            .distinctUntilChanged() // 상태 변화가 있을 때만 업데이트
            .collect { isScrolled ->
                searchBarVisible = !isScrolled // 스크롤되면 searchBar 숨기기
            }
    }
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
            visible = searchBarVisible,
        ) {
            SearchBar(
                currentSection = currentSection,
                onClickChip = onClickSection,
                onSearch = onSearchClick,
            )
        }
    }
}

@Composable
private fun NewsContent(
    onClickNews: (String) -> Unit,
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
        items(
            items = newsList,
            key = { it.title },
        ) {
            NewsCard(newsModel = it, onClick = { onClickNews(it.link) })
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
//        TopicScreen(
//            newsList = mutableStateListOf(),
//            currentSection = SectionChip.ECONOMY,
//            onClickSection = {},
//            onSearchClick = {},
//        )
    }
}
