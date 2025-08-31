package org.bmsk.lifemash.feature.feed

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.outlined.DynamicFeed
import androidx.compose.material.icons.outlined.EmojiEmotions
import androidx.compose.material.icons.outlined.FormatQuote
import androidx.compose.material.icons.outlined.Gavel
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.HealthAndSafety
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material.icons.outlined.SportsSoccer
import androidx.compose.material.icons.outlined.Woman
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toPersistentList
import org.bmsk.lifemash.domain.core.model.Article
import org.bmsk.lifemash.domain.core.model.ArticleCategory
import org.bmsk.lifemash.domain.core.model.ArticleId
import java.net.URI
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

internal sealed interface LoadState {
    data object NotLoaded : LoadState
    data object Loading : LoadState
    data object Loaded : LoadState
    data class Error(val throwable: Throwable) : LoadState
}

internal data class FeedUiState(
    val selectedCategory: ArticleCategory,
    val articlesById: PersistentMap<ArticleId, ArticleUi>,
    val idsByCategory: PersistentMap<ArticleCategory, PersistentList<ArticleId>>,
    val loadStateByCategory: PersistentMap<ArticleCategory, LoadState>,
    val visibleArticles: PersistentList<ArticleUi> = persistentListOf(),
    val isSearchMode: Boolean = false,
    val queryText: String = "",
) {
    companion object {
        val Initial = FeedUiState(
            selectedCategory = ArticleCategory.ALL,
            articlesById = persistentMapOf(),
            idsByCategory = ArticleCategory.entries
                .fold(persistentMapOf()) { acc, cat -> acc.put(cat, persistentListOf()) },
            loadStateByCategory = ArticleCategory.entries
                .fold(persistentMapOf()) { acc, cat -> acc.put(cat, LoadState.NotLoaded) },
        )
    }
}

internal data class ArticleUi(
    val id: ArticleId,
    val publisher: String,
    val title: String,
    val summary: String,
    val link: String,
    val image: String?,
    val publishedAtRelative: String,
    val publishedAtInstant: Instant,
    val host: String,
    val categories: PersistentList<ArticleCategory>,
    val isScrapped: Boolean,
) {
    companion object {
        private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            .withZone(ZoneId.systemDefault())

        fun from(article: Article, isScrapped: Boolean): ArticleUi {
            return ArticleUi(
                id = article.id,
                publisher = article.publisher.name,
                title = article.title,
                summary = article.summary,
                link = article.link.value,
                image = article.image?.value,
                publishedAtRelative = formatter.format(article.publishedAt),
                publishedAtInstant = article.publishedAt,
                host = URI(article.link.value).host ?: article.link.value,
                categories = article.categories.toPersistentList(),
                isScrapped = isScrapped,
            )
        }
    }
}

internal data class CategoryStyle(
    @param:StringRes val labelRes: Int,
    val icon: ImageVector,
    val color: Color
)

private val CATEGORY_STYLES: PersistentMap<ArticleCategory, CategoryStyle> = persistentMapOf(
    ArticleCategory.ALL to CategoryStyle(
        R.string.feature_feed_cat_all,
        Icons.Outlined.DynamicFeed,
        Color(0xFF9E9E9E)
    ),
    ArticleCategory.POLITICS to CategoryStyle(
        R.string.feature_feed_cat_politics,
        Icons.Outlined.Gavel,
        Color(0xFFEF5350)
    ),
    ArticleCategory.ECONOMY to CategoryStyle(
        R.string.feature_feed_cat_economy,
        Icons.AutoMirrored.Outlined.TrendingUp,
        Color(0xFFFFB300)
    ),
    ArticleCategory.SOCIETY to CategoryStyle(
        R.string.feature_feed_cat_society,
        Icons.Outlined.Groups,
        Color(0xFF42A5F5)
    ),
    ArticleCategory.INTERNATIONAL to CategoryStyle(
        R.string.feature_feed_cat_international,
        Icons.Outlined.Public,
        Color(0xFF66BB6A)
    ),
    ArticleCategory.SPORTS to CategoryStyle(
        R.string.feature_feed_cat_sports,
        Icons.Outlined.SportsSoccer,
        Color(0xFFAB47BC)
    ),
    ArticleCategory.CULTURE to CategoryStyle(
        R.string.feature_feed_cat_culture,
        Icons.Outlined.Palette,
        Color(0xFF5C6BC0)
    ),
    ArticleCategory.ENTERTAINMENT to CategoryStyle(
        R.string.feature_feed_cat_entertainment,
        Icons.Outlined.Movie,
        Color(0xFFFF7043)
    ),
    ArticleCategory.TECH to CategoryStyle(
        R.string.feature_feed_cat_tech,
        Icons.Outlined.Memory,
        Color(0xFF26C6DA)
    ),
    ArticleCategory.SCIENCE to CategoryStyle(
        R.string.feature_feed_cat_science,
        Icons.Outlined.Science,
        Color(0xFF26C6DA)
    ),
    ArticleCategory.COLUMN to CategoryStyle(
        R.string.feature_feed_cat_column,
        Icons.Outlined.FormatQuote,
        Color(0xFF8D6E63)
    ),
    ArticleCategory.PEOPLE to CategoryStyle(
        R.string.feature_feed_cat_people,
        Icons.Outlined.Person,
        Color(0xFF7E57C2)
    ),
    ArticleCategory.HEALTH to CategoryStyle(
        R.string.feature_feed_cat_health,
        Icons.Outlined.HealthAndSafety,
        Color(0xFF26A69A)
    ),
    ArticleCategory.MEDICAL to CategoryStyle(
        R.string.feature_feed_cat_medical,
        Icons.Outlined.MedicalServices,
        Color(0xFF26A69A)
    ),
    ArticleCategory.WOMEN to CategoryStyle(
        R.string.feature_feed_cat_women,
        Icons.Outlined.Woman,
        Color(0xFFEC407A)
    ),
    ArticleCategory.CARTOON to CategoryStyle(
        R.string.feature_feed_cat_cartoon,
        Icons.Outlined.EmojiEmotions,
        Color(0xFF009688)
    ),
).also { map ->
    check(map.size == ArticleCategory.entries.size) {
        "CATEGORY_STYLES size=${map.size} mismatches ArticleCategory size=${ArticleCategory.entries.size}"
    }
}

internal val ArticleCategory.style: CategoryStyle
    get() = CATEGORY_STYLES[this]
        ?: error("Missing CategoryStyle for $this. Did you update CATEGORY_STYLES?")