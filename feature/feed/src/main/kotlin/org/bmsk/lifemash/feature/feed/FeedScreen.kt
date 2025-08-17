package org.bmsk.lifemash.feature.feed

import android.content.res.Configuration
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import org.bmsk.lifemash.core.designsystem.component.HSpacer
import org.bmsk.lifemash.core.designsystem.theme.LifeMashTheme
import org.bmsk.lifemash.core.repo.article.api.ArticleCategory

// ---------------------------
// Top-level screen
// ---------------------------

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun FeedScreen(
    modifier: Modifier = Modifier,
    selectedCategory: ArticleCategory = ArticleCategory.ALL,
    categories: List<ArticleCategory> = ArticleCategory.entries,
    articles: PersistentList<ArticleUi> = persistentListOf(),
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

/** 단일 카드용 */
private class ArticlePreviewParameter : PreviewParameterProvider<ArticleUi> {
    override val values = sequenceOf(
        ArticleUi(
            id = "02f9f94fd7c7f804fd8f57e7cabebbf3a5272e7c",
            publisher = "뉴시스",
            title = "대구 수성구서 60㎝ 땅 꺼짐 사고…\"상수도관 누수 탓\"",
            summary = "[대구=뉴시스]정재익 기자 = 대구 수성구의 한 주차장 진입로에서 땅 꺼짐 사고가 발생했다.  16일 수성구 등에 따르면 이날 오후 1시10분께 수성구 욱수동의 한 교회 주차장 진입로에서 가로·세로 각 50㎝의 지반이 60㎝가량 내려앉았다.  다행히 침하로 인한 인명피해는 없다. 당국은 안전고깔을 설치하는 등 임시 조치를 했다.  수성구는 인근 상수도관 누수로 인한 땅 꺼짐 현상으로 추측하고 정확한 경위를 파악 중이다.  ◎공감언론 뉴시스 jjikk@newsis.com",
            link = "https://www.newsis.com/view/NISX20250816_0003292356",
            image = "https://image.newsis.com/2025/07/30/NISI20250730_0001906581_web.jpg?rnd=20250730132651",
            publishedAt = 1755369802L, // raw=...
            host = "www.newsis.com",
            categories = persistentListOf(ArticleCategory.SOCIETY)
        )
    )
}

/** 리스트/피드용 */
private class ArticlesPreviewParameter : PreviewParameterProvider<List<ArticleUi>> {
    override val values = sequenceOf(
        listOf(
            // Article 1
            ArticleUi(
                id = "02f9f94fd7c7f804fd8f57e7cabebbf3a5272e7c",
                publisher = "뉴시스",
                title = "대구 수성구서 60㎝ 땅 꺼짐 사고…\"상수도관 누수 탓\"",
                summary = "[대구=뉴시스]정재익 기자 = 대구 수성구의 한 주차장 진입로에서 땅 꺼짐 사고가 발생했다...",
                link = "https://www.newsis.com/view/NISX20250816_0003292356",
                image = "https://image.newsis.com/2025/07/30/NISI20250730_0001906581_web.jpg?rnd=20250730132651",
                publishedAt = 1755369802L,
                host = "www.newsis.com",
                categories = persistentListOf(ArticleCategory.SOCIETY)
            ),
            // Article 3
            ArticleUi(
                id = "14d3e90c4710c203bd999c487c333ad00da89f81",
                publisher = "동아일보",
                title = "장동혁, 특검 앞 1인 시위…“정치특검 광기 도 넘어”",
                summary = "장동혁 국민의힘 당대표 후보는 16일 특검팀 사무실 앞에 나와 1인 시위를 진행하면서 “정치특검의 광기가 도를 넘었다”고 밝혔다...",
                link = "https://www.donga.com/news/Politics/article/all/20250816/132196672/1",
                image = "https://dimg.donga.com/i/150/150/90/wps/NEWS/IMAGE/2025/08/16/132196673.1.jpg",
                publishedAt = 1755369018L,
                host = "www.donga.com",
                categories = persistentListOf(ArticleCategory.ALL, ArticleCategory.POLITICS)
            ),
            // Article 5
            ArticleUi(
                id = "263debf456846f254361214548a784e280fdc29b",
                publisher = "동아일보",
                title = "“삼성전자 제쳤다”…4대금융 상반기 급여 1억",
                summary = "올해 상반기 우리나라 주요 시중은행 직원들의 평균 급여가 역대 최고 수준을 기록했다...",
                link = "https://www.donga.com/news/Economy/article/all/20250816/132196667/1",
                image = "https://dimg.donga.com/i/150/150/90/wps/NEWS/IMAGE/2025/08/16/132196668.1.jpg",
                publishedAt = 1755368675L,
                host = "www.donga.com",
                categories = persistentListOf(ArticleCategory.ECONOMY, ArticleCategory.ALL)
            ),
            // Article 10
            ArticleUi(
                id = "88a8bd2298442d107b38fafb8734f3a501fe735f",
                publisher = "뉴시스",
                title = "日, 한국 조사선 독도 주변 해양조사 활동에 강력 항의",
                summary = "[서울=뉴시스]박지혁 기자 = 일본 정부가 한국 조사선이 독도 주변에서 해양조사 활동을 한 것을 두고 항의했다...",
                link = "https://www.newsis.com/view/NISX20250816_0003292350",
                image = "https://image.newsis.com/2025/04/09/NISI20250409_0001812899_web.jpg?rnd=20250409092704",
                publishedAt = 1755367447L,
                host = "www.newsis.com",
                categories = persistentListOf(
                    ArticleCategory.INTERNATIONAL,
                    ArticleCategory.POLITICS
                )
            ),
            // Article 17
            ArticleUi(
                id = "1e13ce051109b29bd8cd4b3f5d1a6d31f341651c",
                publisher = "서울신문",
                title = "[속보] 열흘 ‘황금연휴’ 무산…정부 “10월 10일 임시공휴일 검토 안해”",
                summary = "오는 10월 10일 임시공휴일 지정 시 열흘간의 황금연휴가 가능해 기대감이 커지고 있는 가운데 정부가 '이를 검토하지 않는다'고 선을 그었다...",
                link = "https://www.seoul.co.kr/news/newsView.php?id=20250816500031",
                image = "https://img.seoul.co.kr/img/upload/2025/08/11/SSC_20250811070507_V.jpg",
                publishedAt = 1755366823L,
                host = "www.seoul.co.kr",
                categories = persistentListOf(ArticleCategory.SOCIETY)
            )
        )
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun Preview_ArticleList(
    @PreviewParameter(ArticlesPreviewParameter::class) articles: List<ArticleUi>
) {
    LifeMashTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            articles.forEach { ArticleCard(article = it, onOpen = {}) }
        }
    }
}

/** 만약 FeedScreen이 상태를 받는다면 이렇게 연결해서 쓸 수 있어요 */
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun Preview_FeedScreen_WithParams(
    @PreviewParameter(ArticlesPreviewParameter::class) articles: List<ArticleUi>
) {
    LifeMashTheme {
        FeedScreen(
            modifier = Modifier.fillMaxSize(),
            articles = articles.toPersistentList(),
        )
    }
}