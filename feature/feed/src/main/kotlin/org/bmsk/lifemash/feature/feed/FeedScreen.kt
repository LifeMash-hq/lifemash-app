package org.bmsk.lifemash.feature.feed

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import org.bmsk.lifemash.core.designsystem.component.HSpacer
import org.bmsk.lifemash.core.repo.article.api.ArticleCategory
import kotlin.random.Random

// ---------------------------
// Top-level screen
// ---------------------------

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun FeedScreen(
    modifier: Modifier = Modifier,
    selectedCategory: ArticleCategory = ArticleCategory.ALL,
    categories: List<ArticleCategory> = ArticleCategory.entries,
    articles: PersistentList<ArticleUi> = SampleData.articles.toPersistentList(),
    isSearchMode: Boolean = false,
    queryText: String = "",
    onArticleOpen: (ArticleUi) -> Unit = {},
    onQueryTextChange: (String) -> Unit = {},
    onQueryTextClear: () -> Unit = {},
    onSearchModeChange: (Boolean) -> Unit = {},
    onSearchClick: (String) -> Unit = {},
    onCategorySelect: (ArticleCategory) -> Unit = {}
) {
    val listState = rememberLazyListState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.systemBars,
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            LazyColumn(
                state = listState,
            ) {
                if (articles.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillParentMaxSize())
                    }
                } else {
                    items(
                        items = articles,
                        key = { it.id }
                    ) { article ->
                        ArticleCard(article, onArticleOpen)
                    }
                }

                item { HSpacer(80.dp) }
            }

            // Floating gradient bar hint
            TopFade(modifier = Modifier.align(Alignment.TopCenter))

            CategoryBar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(12.dp)),
                selectedCategory = selectedCategory,
                categories = categories,
                isSearchMode = isSearchMode,
                queryText = queryText,
                onQueryTextChange = onQueryTextChange,
                onQueryTextClear = onQueryTextClear,
                onSearchModeChange = onSearchModeChange,
                onSearchClick = onSearchClick,
                onCategorySelect = onCategorySelect,
            )
        }
    }
}

// ---------------------------
// Article card
// ---------------------------

@Composable
internal fun ArticleCard(
    article: ArticleUi,
    onOpen: (ArticleUi) -> Unit
) {
    val mainCat = article.categories.firstOrNull() ?: ArticleCategory.ALL
    val style = remember(mainCat) { mainCat.style }

    Card(
        onClick = { onOpen(article) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .shadow(1.dp, RoundedCornerShape(22.dp)),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(Modifier.fillMaxWidth()) {
            ArticleImage(url = article.image)
            Column(Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        style.icon,
                        contentDescription = null,
                        tint = style.color,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        article.publisher,
                        style = MaterialTheme.typography.labelMedium,
                        color = style.color
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "· ${article.host}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    article.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    article.summary,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun ArticleImage(url: String?) {
    val painter = rememberAsyncImagePainter(model = url)
    // 아래 조건이 항상 false라는 Lint 등장 이유?
    val isLoading = painter.state is AsyncImagePainter.State.Loading

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        if (url != null) {
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        if (isLoading || url == null) {
            ShimmerOverlay()
        }
    }
}

// ---------------------------
// Visual polish
// ---------------------------

@Composable
private fun gradientBG(): Brush = Brush.verticalGradient(
    colors = listOf(
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        MaterialTheme.colorScheme.background
    )
)

@Composable
private fun TopFade(modifier: Modifier = Modifier, height: Dp = 32.dp) {
    Box(
        modifier
            .fillMaxWidth()
            .height(height)
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.background,
                        Color.Transparent
                    )
                )
            )
    )
}

@Composable
private fun ShimmerOverlay() {
    // 간단한 shimmer 효과
    val infinite = rememberInfiniteTransition(label = "shimmer")
    val x by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = LinearEasing
            )
        ),
        label = "shimmerX"
    )
    val brush = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        ),
        start = Offset(x, 0f),
        end = Offset(x + 300f, 300f)
    )
    Box(
        Modifier
            .fillMaxSize()
            .background(brush)
    )
}

// ---------------------------
// Preview (Design-only)
// ---------------------------

private object SampleData {

    private fun fake(id: Int, category: ArticleCategory) = ArticleUi(
        id = "id_$id",
        publisher = listOf("한겨레", "경향신문", "조선일보", "동아일보").random(),
        title = "${category.name} — 헤드라인 $id",
        summary = "앱 미리보기를 위한 더미 요약 텍스트입니다. 두세 줄의 브리핑을 가정합니다.",
        link = "https://example.com/article/$id",
        image = if (id % 3 == 0) null else "https://picsum.photos/seed/${category}_$id/800/450",
        publishedAt = System.currentTimeMillis() / 1000,
        host = "example.com",
        categories = persistentListOf(category)
    )

    val articles = ArticleCategory.entries.mapIndexed { index, category ->
        fake(index + 1 + Random.nextInt(100), category)
    }.let {
        it + it.shuffled().take(12)
    }
}

@Preview(showBackground = true)
@Composable
fun Preview_NewsScreen_Light() {
    MaterialTheme(colorScheme = lightColorScheme()) {
        FeedScreen(modifier = Modifier.fillMaxSize())
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun Preview_NewsScreen_Dark() {
    MaterialTheme(colorScheme = darkColorScheme()) {
        FeedScreen(modifier = Modifier.fillMaxSize())
    }
}

@Preview
@Composable
fun Preview_ArticleCard() {
    MaterialTheme(colorScheme = lightColorScheme()) {
        ArticleCard(
            article = ArticleUi(
                id = "x",
                publisher = "한겨레",
                title = "문화 — 주말 가볼만한 전시 10",
                summary = "전시회 라인업이 풍성합니다. 서울 시내 주요 미술관에서...",
                link = "https://example.com",
                image = "https://picsum.photos/seed/culture/1200/800",
                publishedAt = System.currentTimeMillis() / 1000,
                host = "hani.co.kr",
                categories = persistentListOf(ArticleCategory.CULTURE)
            ),
            onOpen = {}
        )
    }
}
