package org.bmsk.lifemash.feed.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BrokenImage
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import org.bmsk.lifemash.feature.designsystem.component.ScrapButton
import org.bmsk.lifemash.feature.designsystem.component.SpacerH
import org.bmsk.lifemash.model.ArticleCategory

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun FeedScreen(
    modifier: Modifier = Modifier,
    onNotificationClick: () -> Unit = {},
    selectedCategory: ArticleCategory = ArticleCategory.ALL,
    categories: List<ArticleCategory> = ArticleCategory.entries,
    articles: PersistentList<ArticleUiState> = persistentListOf(),
    isSearchMode: Boolean = false,
    queryText: String = "",
    onArticleOpen: (ArticleUiState) -> Unit = {},
    onScrapClick: (ArticleUiState) -> Unit = {},
    onQueryTextChange: (String) -> Unit = {},
    onQueryTextClear: () -> Unit = {},
    onSearchModeChange: (Boolean) -> Unit = {},
    onSearchClick: (String) -> Unit = {},
    onCategorySelect: (ArticleCategory) -> Unit = {},
    subscribedCategories: Set<ArticleCategory> = emptySet(),
    onSetSubscribedCategories: (Set<ArticleCategory>) -> Unit = {},
) {
    val listState = rememberLazyListState()
    var showSubscriptionDialog by remember { mutableStateOf(false) }

    if (showSubscriptionDialog) {
        CategorySubscriptionDialog(
            subscribedCategories = subscribedCategories,
            onConfirm = { categories ->
                onSetSubscribedCategories(categories)
                showSubscriptionDialog = false
            },
            onDismiss = { showSubscriptionDialog = false },
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal),
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            LazyColumn(state = listState) {
                if (articles.isEmpty()) {
                    item { Box(modifier = Modifier.fillParentMaxSize()) }
                } else {
                    itemsIndexed(items = articles, key = { _, it -> it.article.id.toString() }) { index, article ->
                        val variant = when (index) {
                            0 -> ArticleCardStyle.Headline
                            1, 2 -> ArticleCardStyle.Compact
                            else -> ArticleCardStyle.Standard
                        }
                        ArticleCard(
                            article = article,
                            variant = variant,
                            onOpen = onArticleOpen,
                            onScrapClick = { onScrapClick(article) },
                        )
                    }
                }
                item { SpacerH(80.dp) }
            }

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
                onSubscriptionSettingClick = { showSubscriptionDialog = true },
                onNotificationClick = onNotificationClick,
            )
        }
    }
}

enum class ArticleCardStyle { Headline, Compact, Standard }

@Composable
internal fun ArticleCard(
    article: ArticleUiState,
    variant: ArticleCardStyle = ArticleCardStyle.Standard,
    onOpen: (ArticleUiState) -> Unit,
    onScrapClick: () -> Unit,
) {
    val mainCat = article.article.categories.firstOrNull() ?: ArticleCategory.ALL
    val style = remember(mainCat) { mainCat.style }
    val isDark = isSystemInDarkTheme()

    when (variant) {
        ArticleCardStyle.Headline -> HeadlineCard(article, style, isDark, onOpen, onScrapClick)
        ArticleCardStyle.Compact -> CompactCard(article, style, isDark, onOpen, onScrapClick)
        ArticleCardStyle.Standard -> StandardCard(article, style, isDark, onOpen, onScrapClick)
    }
}

@Composable
private fun StandardCard(
    article: ArticleUiState,
    style: CategoryStyle,
    isDark: Boolean,
    onOpen: (ArticleUiState) -> Unit,
    onScrapClick: () -> Unit,
) {
    Card(
        onClick = { onOpen(article) },
        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 8.dp).shadow(1.dp, RoundedCornerShape(22.dp)),
        shape = RoundedCornerShape(22.dp),
    ) {
        Box(Modifier.fillMaxWidth()) {
            Column(Modifier.fillMaxWidth()) {
                ArticleImage(url = article.article.image, contentDescription = article.article.title)
                ArticleTextContent(article, style)
            }
            ScrapOverlay(isDark, article.isScrapped, onScrapClick, Modifier.align(Alignment.TopEnd))
        }
    }
}

@Composable
private fun HeadlineCard(
    article: ArticleUiState,
    style: CategoryStyle,
    isDark: Boolean,
    onOpen: (ArticleUiState) -> Unit,
    onScrapClick: () -> Unit,
) {
    Card(
        onClick = { onOpen(article) },
        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 8.dp).shadow(2.dp, RoundedCornerShape(22.dp)),
        shape = RoundedCornerShape(22.dp),
    ) {
        Box(Modifier.fillMaxWidth()) {
            ArticleImage(url = article.article.image, contentDescription = article.article.title, height = 240.dp)
            // 하단 그라데이션 오버레이
            Box(
                Modifier.fillMaxWidth().height(140.dp).align(Alignment.BottomCenter)
                    .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))))
            )
            // 제목 + 카테고리 오버레이
            Column(
                Modifier.align(Alignment.BottomStart).fillMaxWidth().padding(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(style.icon, contentDescription = style.label, tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(article.article.publisher, style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.8f))
                    Spacer(Modifier.width(8.dp))
                    Text("· ${article.host}", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.6f))
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    article.article.title,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.White,
                )
            }
            ScrapOverlay(isDark, article.isScrapped, onScrapClick, Modifier.align(Alignment.TopEnd))
        }
    }
}

@Composable
private fun CompactCard(
    article: ArticleUiState,
    style: CategoryStyle,
    isDark: Boolean,
    onOpen: (ArticleUiState) -> Unit,
    onScrapClick: () -> Unit,
) {
    Card(
        onClick = { onOpen(article) },
        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 6.dp).shadow(1.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f).padding(end = 12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(style.icon, contentDescription = style.label, tint = style.color, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(article.article.publisher, style = MaterialTheme.typography.labelSmall, color = style.color)
                    Spacer(Modifier.width(6.dp))
                    Text(article.publishedAtRelative, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    article.article.title,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = if (article.isRead) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("${article.host}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline, modifier = Modifier.weight(1f))
                    ScrapButton(isScrapped = article.isScrapped, onClick = onScrapClick, modifier = Modifier.size(20.dp))
                }
            }
            if (article.article.image != null) {
                ArticleImage(
                    url = article.article.image,
                    contentDescription = article.article.title,
                    height = 80.dp,
                    modifier = Modifier.size(80.dp).clip(RoundedCornerShape(12.dp)),
                )
            }
        }
    }
}

@Composable
private fun ArticleTextContent(article: ArticleUiState, style: CategoryStyle) {
    Column(Modifier.padding(14.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(style.icon, contentDescription = style.label, tint = style.color, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text(article.article.publisher, style = MaterialTheme.typography.labelMedium, color = style.color)
            Spacer(Modifier.width(8.dp))
            Text("· ${article.host}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
            Spacer(modifier = Modifier.weight(1f))
            Text(article.publishedAtRelative, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
        }
        Spacer(Modifier.height(6.dp))
        Text(
            article.article.title, style = MaterialTheme.typography.titleMedium, maxLines = 2, overflow = TextOverflow.Ellipsis,
            color = if (article.isRead) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(4.dp))
        Text(article.article.summary, style = MaterialTheme.typography.bodyMedium, maxLines = 3, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun ScrapOverlay(isDark: Boolean, isScrapped: Boolean, onScrapClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.padding(8.dp),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surface.copy(alpha = if (isDark) 0.55f else 0.72f),
        tonalElevation = 2.dp,
    ) {
        Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
            ScrapButton(isScrapped = isScrapped, onClick = onScrapClick, modifier = Modifier.size(24.dp))
        }
    }
}

@Composable
private fun ArticleImage(url: String?, contentDescription: String? = null, height: Dp = 180.dp, modifier: Modifier = Modifier) {
    val painter = rememberAsyncImagePainter(model = url)
    val painterState by painter.state.collectAsState()
    val isLoading = painterState is AsyncImagePainter.State.Loading

    val isError = painterState is AsyncImagePainter.State.Error

    Box(modifier = modifier.fillMaxWidth().height(height).background(MaterialTheme.colorScheme.surfaceVariant)) {
        if (url != null && !isError) {
            Image(painter = painter, contentDescription = contentDescription, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        }
        if (isError) {
            Icon(
                imageVector = Icons.Outlined.BrokenImage,
                contentDescription = "이미지 로딩 실패",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(48.dp).align(Alignment.Center),
            )
        }
        if (isLoading || url == null) { ShimmerOverlay() }
    }
}

@Composable
private fun TopFade(modifier: Modifier = Modifier, height: Dp = 32.dp) {
    Box(
        modifier.fillMaxWidth().height(height).background(
            Brush.verticalGradient(listOf(MaterialTheme.colorScheme.background, Color.Transparent))
        )
    )
}

@Composable
private fun ShimmerOverlay() {
    val infinite = rememberInfiniteTransition(label = "shimmer")
    val x by infinite.animateFloat(
        initialValue = 0f, targetValue = 1000f,
        animationSpec = infiniteRepeatable(animation = tween(durationMillis = 1200, easing = LinearEasing)),
        label = "shimmerX"
    )
    val brush = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        ),
        start = Offset(x, 0f), end = Offset(x + 300f, 300f)
    )
    Box(Modifier.fillMaxSize().background(brush))
}
