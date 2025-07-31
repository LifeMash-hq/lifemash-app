package org.bmsk.lifemash.feature.scrap

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import org.bmsk.lifemash.core.designsystem.theme.LifeMashTheme
import org.bmsk.lifemash.core.model.DateParser
import org.bmsk.lifemash.core.model.NewsModel
import org.bmsk.lifemash.feature.scrap.component.ScrapNewsItem
import org.bmsk.lifemash.feature.scrap.component.rememberScrapNewsItemState

@Composable
internal fun ScrapNewsScreen(
    scrapUiState: ScrapUiState,
    onClickNews: (url: String) -> Unit,
    deleteScrapNews: (NewsModel) -> Unit,
) {
    when (scrapUiState) {
        is ScrapUiState.NewsLoading -> ScrapNewsLoadingScreen()
        is ScrapUiState.NewsLoaded -> ScrapNewsLoadedScreen(
            onClickNews = onClickNews,
            scrapNewsList = scrapUiState.scraps,
            deleteScrapNews = deleteScrapNews,
        )

        is ScrapUiState.NewsEmpty -> ScrapNewsEmptyScreen()
        is ScrapUiState.Error -> ScrapNewsErrorScreen()
    }
}

@Composable
internal fun ScrapNewsLoadingScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center),
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
internal fun ScrapNewsLoadedScreen(
    onClickNews: (url: String) -> Unit,
    scrapNewsList: PersistentList<NewsModel>,
    deleteScrapNews: (NewsModel) -> Unit,
) {
    val scrapNewsItemState = rememberScrapNewsItemState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 12.dp)
    ) {
        // 상단 헤더
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 28.dp, bottom = 12.dp)
        ) {
            Text(
                text = stringResource(R.string.feature_scrap_header),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = stringResource(R.string.feature_scrap_count, scrapNewsList.size),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )

        // LazyColumn(뉴스 리스트)
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 36.dp)
        ) {
            items(scrapNewsList) { news ->
                ScrapNewsItem(
                    newsModel = news,
                    state = scrapNewsItemState,
                    onClick = { onClickNews(news.link) },
                    onClickDelete = { deleteScrapNews(news) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
internal fun ScrapNewsEmptyScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(72.dp)
            )
            Text(
                text = stringResource(R.string.feature_scrap_empty_news),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 16.dp)
            )
            Text(
                text = stringResource(R.string.feature_scrap_news_here), // "스크랩한 뉴스가 여기에 표시됩니다.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
internal fun ScrapNewsErrorScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(72.dp)
            )
            Text(
                text = stringResource(R.string.feature_scrap_error_occurrence),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 16.dp)
            )
            Text(
                text = stringResource(R.string.feature_scrap_retry),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

private class ScrapUiStateProvider : PreviewParameterProvider<ScrapUiState> {
    override val values = sequenceOf(
        ScrapUiState.NewsLoading,
        ScrapUiState.NewsLoaded(
            persistentListOf(
                NewsModel(
                    title = "프리뷰 뉴스 제목",
                    link = "",
                    pubDate = DateParser.parseDate("Tue, 20 Jun 2023 02:57:43 GMT"),
                    imageUrl = ""
                ),
                NewsModel(
                    title = "또 다른 뉴스 제목",
                    link = "",
                    pubDate = DateParser.parseDate("Tue, 21 Jun 2023 02:57:43 GMT"),
                    imageUrl = ""
                )
            )
        ),
        ScrapUiState.NewsEmpty, // 만약 Empty 상태도 있다면
        ScrapUiState.Error(Throwable("프리뷰용 에러")),
    )
}

@Composable
@Preview(showBackground = true, name = "ScrapNewsScreen All States")
private fun ScrapNewsScreenPreview(
    @PreviewParameter(ScrapUiStateProvider::class) state: ScrapUiState
) {
    LifeMashTheme {
        ScrapNewsScreen(
            scrapUiState = state,
            onClickNews = {},
            deleteScrapNews = {},
        )
    }
}
