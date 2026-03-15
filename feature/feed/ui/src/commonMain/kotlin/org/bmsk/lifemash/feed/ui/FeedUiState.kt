package org.bmsk.lifemash.feed.ui

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
import org.bmsk.lifemash.model.Article
import org.bmsk.lifemash.model.ArticleCategory
import org.bmsk.lifemash.model.ArticleId
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

sealed interface LoadState {
    data object NotLoaded : LoadState
    data object Loading : LoadState
    data object Loaded : LoadState
    data class Error(val throwable: Throwable) : LoadState
}

data class FeedUiState(
    val selectedCategory: ArticleCategory,
    val articlesById: PersistentMap<ArticleId, ArticleUiState>,
    val idsByCategory: PersistentMap<ArticleCategory, PersistentList<ArticleId>>,
    val loadStateByCategory: PersistentMap<ArticleCategory, LoadState>,
    val visibleArticles: PersistentList<ArticleUiState> = persistentListOf(),
    val isSearchMode: Boolean = false,
    val queryText: String = "",
    val searchResults: PersistentList<ArticleUiState> = persistentListOf(),
    val subscribedCategories: Set<ArticleCategory> = emptySet(),
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

data class ArticleUiState(
    val article: Article,
    val publishedAtRelative: String,
    val host: String,
    val isScrapped: Boolean,
    val isRead: Boolean = false,
) {
    val id: ArticleId get() = article.id

    companion object {
        fun from(article: Article, isScrapped: Boolean, isRead: Boolean = false): ArticleUiState {
            return ArticleUiState(
                article = article,
                publishedAtRelative = article.publishedAt
                    .toLocalDateTime(TimeZone.currentSystemDefault())
                    .let { "${it.year.toString().padStart(4, '0')}-${it.monthNumber.toString().padStart(2, '0')}-${it.dayOfMonth.toString().padStart(2, '0')} ${it.hour.toString().padStart(2, '0')}:${it.minute.toString().padStart(2, '0')}" },
                host = article.link.value
                    .substringAfter("://")
                    .substringBefore("/"),
                isScrapped = isScrapped,
                isRead = isRead,
            )
        }
    }
}

internal data class CategoryStyle(
    val label: String,
    val icon: ImageVector,
    val color: Color
)

private val CATEGORY_STYLES: PersistentMap<ArticleCategory, CategoryStyle> = persistentMapOf(
    ArticleCategory.ALL to CategoryStyle("전체", Icons.Outlined.DynamicFeed, Color(0xFF9E9E9E)),
    ArticleCategory.POLITICS to CategoryStyle("정치", Icons.Outlined.Gavel, Color(0xFFEF5350)),
    ArticleCategory.ECONOMY to CategoryStyle("경제", Icons.AutoMirrored.Outlined.TrendingUp, Color(0xFFFFB300)),
    ArticleCategory.SOCIETY to CategoryStyle("사회", Icons.Outlined.Groups, Color(0xFF42A5F5)),
    ArticleCategory.INTERNATIONAL to CategoryStyle("국제", Icons.Outlined.Public, Color(0xFF66BB6A)),
    ArticleCategory.SPORTS to CategoryStyle("스포츠", Icons.Outlined.SportsSoccer, Color(0xFFAB47BC)),
    ArticleCategory.CULTURE to CategoryStyle("문화", Icons.Outlined.Palette, Color(0xFF5C6BC0)),
    ArticleCategory.ENTERTAINMENT to CategoryStyle("연예", Icons.Outlined.Movie, Color(0xFFFF7043)),
    ArticleCategory.TECH to CategoryStyle("IT", Icons.Outlined.Memory, Color(0xFF26C6DA)),
    ArticleCategory.SCIENCE to CategoryStyle("과학", Icons.Outlined.Science, Color(0xFF26C6DA)),
    ArticleCategory.COLUMN to CategoryStyle("칼럼", Icons.Outlined.FormatQuote, Color(0xFF8D6E63)),
    ArticleCategory.PEOPLE to CategoryStyle("인물", Icons.Outlined.Person, Color(0xFF7E57C2)),
    ArticleCategory.HEALTH to CategoryStyle("건강", Icons.Outlined.HealthAndSafety, Color(0xFF26A69A)),
    ArticleCategory.MEDICAL to CategoryStyle("의학", Icons.Outlined.MedicalServices, Color(0xFF26A69A)),
    ArticleCategory.WOMEN to CategoryStyle("여성", Icons.Outlined.Woman, Color(0xFFEC407A)),
    ArticleCategory.CARTOON to CategoryStyle("만화", Icons.Outlined.EmojiEmotions, Color(0xFF009688)),
).also { map ->
    check(map.size == ArticleCategory.entries.size) {
        "CATEGORY_STYLES size=${map.size} mismatches ArticleCategory size=${ArticleCategory.entries.size}"
    }
}

internal val ArticleCategory.style: CategoryStyle
    get() = CATEGORY_STYLES[this]
        ?: error("Missing CategoryStyle for $this. Did you update CATEGORY_STYLES?")
