package org.bmsk.lifemash.scrap.ui

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
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.PersistentList
import org.bmsk.lifemash.scrap.ui.component.ScrapNewsItem
import org.bmsk.lifemash.scrap.ui.component.rememberScrapNewsItemState

@Composable
internal fun ScrapNewsScreen(
    scrapUiState: ScrapUiState,
    onClickNews: (url: String) -> Unit,
    deleteScrapNews: (ScrapArticleUi) -> Unit,
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
        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
internal fun ScrapNewsLoadedScreen(
    onClickNews: (url: String) -> Unit,
    scrapNewsList: PersistentList<ScrapArticleUi>,
    deleteScrapNews: (ScrapArticleUi) -> Unit,
) {
    val scrapNewsItemState = rememberScrapNewsItemState()

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).statusBarsPadding().padding(horizontal = 12.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 12.dp)
        ) {
            Text(text = "스크랩한 뉴스", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary, modifier = Modifier.weight(1f))
            Text(text = "${scrapNewsList.size}개", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp), color = MaterialTheme.colorScheme.outlineVariant)
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 36.dp)
        ) {
            items(scrapNewsList) { scrap ->
                ScrapNewsItem(
                    scrap = scrap, state = scrapNewsItemState,
                    onClick = { onClickNews(scrap.article.link.value) },
                    onClickDelete = { deleteScrapNews(scrap) },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
internal fun ScrapNewsEmptyScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = Icons.Filled.Info, contentDescription = "스크랩한 뉴스 없음", tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), modifier = Modifier.align(Alignment.CenterHorizontally).size(72.dp))
            Text(text = "스크랩한 뉴스가 없어요", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 16.dp))
            Text(text = "스크랩한 뉴스가 여기에 표시됩니다.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), modifier = Modifier.padding(top = 4.dp))
        }
    }
}

@Composable
internal fun ScrapNewsErrorScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = Icons.Filled.Warning, contentDescription = "오류 발생", tint = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.CenterHorizontally).size(72.dp))
            Text(text = "에러가 발생했습니다.", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 16.dp))
            Text(text = "다시 시도해 주세요.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), modifier = Modifier.padding(top = 4.dp))
        }
    }
}
