package org.bmsk.lifemash.feature.feed

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap
import org.bmsk.lifemash.core.repo.article.api.Article
import org.bmsk.lifemash.core.repo.article.api.ArticleCategory

internal sealed interface LoadState {
    data object NotLoaded : LoadState
    data object Loading : LoadState
    data object Loaded : LoadState
    data class Error(val throwable: Throwable) : LoadState
}

internal data class FeedUiState(
    // 선택된 카테고리
    val selectedCategory: ArticleCategory,
    // Id를 이용해서 아티클 매핑
    val articlesById: PersistentMap<String, ArticleUi>,
    // 카테고리별 아이디 리스트
    val idsByCategory: PersistentMap<ArticleCategory, PersistentList<String>>,
    // 카테고리별 로드 상태
    val loadStateByCategory: PersistentMap<ArticleCategory, LoadState>,

    val isSearchMode: Boolean = false,
    val queryText: String = "",
) {
    companion object {
        val Initial = FeedUiState(
            selectedCategory = ArticleCategory.ALL,
            articlesById = persistentMapOf(),
            idsByCategory = ArticleCategory.entries
                .fold(persistentMapOf<ArticleCategory, PersistentList<String>>()) { acc, cat ->
                    acc.put(cat, persistentListOf())
                },
            loadStateByCategory = ArticleCategory.entries
                .fold(persistentMapOf<ArticleCategory, LoadState>()) { acc, cat ->
                    acc.put(cat, LoadState.NotLoaded)
                }
        )
    }

    val visibleArticles: PersistentList<ArticleUi>
            by lazy {
                if (selectedCategory == ArticleCategory.ALL) {
                    ArticleCategory.entries.asSequence()
                        .flatMap { (idsByCategory[it] ?: persistentListOf()).asSequence() }
                        .distinct()
                        .mapNotNull(articlesById::get)
                        .sortedByDescending { it.publishedAt }
                        .toPersistentList()
                } else {
                    (idsByCategory[selectedCategory] ?: persistentListOf())
                        .mapNotNull(articlesById::get)
                        .toPersistentList()
                }
            }

    fun setCategory(cat: ArticleCategory): FeedUiState = copy(selectedCategory = cat)

    fun setLoading(cat: ArticleCategory): FeedUiState =
        copy(loadStateByCategory = loadStateByCategory.put(cat, LoadState.Loading))

    fun setLoaded(
        cat: ArticleCategory,
        newArticles: List<ArticleUi>
    ): FeedUiState {
        val mergedArticlesById =
            articlesById.putAll(newArticles.associateBy { it.id }.toPersistentMap())
        val newIds = newArticles.map { it.id }.toPersistentList()

        return copy(
            articlesById = mergedArticlesById,
            idsByCategory = idsByCategory.put(cat, newIds),
            loadStateByCategory = loadStateByCategory.put(cat, LoadState.Loaded)
        )
    }

    fun setQuery(query: String): FeedUiState = copy(queryText = query)

    fun setSearchMode(isSearchMode: Boolean): FeedUiState = copy(isSearchMode = isSearchMode)

    fun setError(cat: ArticleCategory, t: Throwable): FeedUiState =
        copy(loadStateByCategory = loadStateByCategory.put(cat, LoadState.Error(t)))
}

internal data class ArticleUi(
    val id: String,
    val publisher: String,
    val title: String,
    val summary: String,
    val link: String,
    val image: String?,
    val publishedAt: Long,
    val host: String,
    val categories: PersistentList<ArticleCategory>
) {
    companion object {
        fun from(article: Article): ArticleUi {
            return ArticleUi(
                id = article.id,
                publisher = article.publisher,
                title = article.title,
                summary = article.summary,
                link = article.link,
                image = article.image,
                publishedAt = article.publishedAt,
                host = article.host,
                categories = article.categories.toPersistentList()
            )
        }
    }
}

internal data class CategoryStyle(
    @param:StringRes val labelRes: Int,
    val icon: ImageVector,
    val color: Color
)

// 불변 맵으로 정의 (컴파일 타임 레벨에서 거의 상수처럼 사용)
private val CATEGORY_STYLES: PersistentMap<ArticleCategory, CategoryStyle> = persistentMapOf(
    ArticleCategory.ALL to CategoryStyle(
        R.string.feature_feed_cat_all,
        Icons.Outlined.Home,
        Color(0xFF9E9E9E)
    ),
    ArticleCategory.POLITICS to CategoryStyle(
        R.string.feature_feed_cat_politics,
        Icons.Outlined.Home,
        Color(0xFFEF5350)
    ),
    ArticleCategory.ECONOMY to CategoryStyle(
        R.string.feature_feed_cat_economy,
        Icons.Outlined.Home,
        Color(0xFFFFB300)
    ),
    ArticleCategory.SOCIETY to CategoryStyle(
        R.string.feature_feed_cat_society,
        Icons.Outlined.Home,
        Color(0xFF42A5F5)
    ),
    ArticleCategory.INTERNATIONAL to CategoryStyle(
        R.string.feature_feed_cat_international,
        Icons.Outlined.Home,
        Color(0xFF66BB6A)
    ),
    ArticleCategory.SPORTS to CategoryStyle(
        R.string.feature_feed_cat_sports,
        Icons.Outlined.Home,
        Color(0xFFAB47BC)
    ),
    ArticleCategory.CULTURE to CategoryStyle(
        R.string.feature_feed_cat_culture,
        Icons.Outlined.Home,
        Color(0xFF5C6BC0)
    ),
    ArticleCategory.ENTERTAINMENT to CategoryStyle(
        R.string.feature_feed_cat_entertainment,
        Icons.Outlined.Home,
        Color(0xFFFF7043)
    ),
    ArticleCategory.TECH to CategoryStyle(
        R.string.feature_feed_cat_tech,
        Icons.Outlined.Home,
        Color(0xFF26C6DA)
    ),
    ArticleCategory.SCIENCE to CategoryStyle(
        R.string.feature_feed_cat_science,
        Icons.Outlined.Home,
        Color(0xFF26C6DA)
    ),
    ArticleCategory.COLUMN to CategoryStyle(
        R.string.feature_feed_cat_column,
        Icons.Outlined.Home,
        Color(0xFF8D6E63)
    ),
    ArticleCategory.PEOPLE to CategoryStyle(
        R.string.feature_feed_cat_people,
        Icons.Outlined.Home,
        Color(0xFF7E57C2)
    ),
    ArticleCategory.HEALTH to CategoryStyle(
        R.string.feature_feed_cat_health,
        Icons.Outlined.Home,
        Color(0xFF26A69A)
    ),
    ArticleCategory.MEDICAL to CategoryStyle(
        R.string.feature_feed_cat_medical,
        Icons.Outlined.Home,
        Color(0xFF26A69A)
    ),
    ArticleCategory.WOMEN to CategoryStyle(
        R.string.feature_feed_cat_women,
        Icons.Outlined.Home,
        Color(0xFFEC407A)
    ),
    ArticleCategory.CARTOON to CategoryStyle(
        R.string.feature_feed_cat_cartoon,
        Icons.Outlined.Home,
        Color(0xFF009688)
    ),
).also { map ->
    // 방어 코드: enum 추가/삭제 시 누락을 조기에 감지
    check(map.size == ArticleCategory.entries.size) {
        "CATEGORY_STYLES size=${map.size} mismatches ArticleCategory size=${ArticleCategory.entries.size}"
    }
}

// 3) 확장 프로퍼티: !! 제거, 누락 시 명확한 에러
internal val ArticleCategory.style: CategoryStyle
    get() = CATEGORY_STYLES[this]
        ?: error("Missing CategoryStyle for $this. Did you update CATEGORY_STYLES?")