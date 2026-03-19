package org.bmsk.lifemash.feed.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import org.bmsk.lifemash.model.Article
import org.bmsk.lifemash.model.ArticleCategory
import org.bmsk.lifemash.model.ArticleId
import org.bmsk.lifemash.model.ArticleUrl
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours

private val previewNow = Clock.System.now()

private val previewArticles = listOf(
    ArticleUiState(
        article = Article(
            id = ArticleId.from("1"),
            publisher = "중앙일보",
            title = "AI 기술이 바꾸는 일상, 올해 주목할 트렌드 5가지",
            summary = "인공지능 기술이 생활 곳곳에 스며들며 새로운 변화를 만들어가고 있다. 전문가들이 꼽은 올해 가장 주목할 AI 트렌드를 살펴본다.",
            link = ArticleUrl.from("https://news.example.com/ai-trends"),
            image = null,
            publishedAt = previewNow - 2.hours,
            categories = listOf(ArticleCategory.TECH),
        ),
        publishedAtRelative = "2시간 전",
        host = "news.example.com",
        isScrapped = false,
    ),
    ArticleUiState(
        article = Article(
            id = ArticleId.from("2"),
            publisher = "한겨레",
            title = "봄 축제 시즌 개막... 전국 주요 행사 안내",
            summary = "전국 곳곳에서 봄 축제가 시작된다.",
            link = ArticleUrl.from("https://culture.example.com/spring"),
            image = null,
            publishedAt = previewNow - 5.hours,
            categories = listOf(ArticleCategory.CULTURE),
        ),
        publishedAtRelative = "5시간 전",
        host = "culture.example.com",
        isScrapped = true,
    ),
    ArticleUiState(
        article = Article(
            id = ArticleId.from("3"),
            publisher = "조선일보",
            title = "프로야구 개막전 관전 포인트 총정리",
            summary = "올시즌 프로야구의 주요 관전 포인트를 정리했다.",
            link = ArticleUrl.from("https://sports.example.com/baseball"),
            image = null,
            publishedAt = previewNow - 8.hours,
            categories = listOf(ArticleCategory.SPORTS),
        ),
        publishedAtRelative = "8시간 전",
        host = "sports.example.com",
        isScrapped = false,
        isRead = true,
    ),
).toPersistentList()

@Preview(showBackground = true)
@Composable
private fun FeedScreenPreview() {
    FeedScreen(
        articles = previewArticles,
        selectedCategory = ArticleCategory.ALL,
    )
}

@Preview(showBackground = true)
@Composable
private fun FeedScreenEmptyPreview() {
    FeedScreen(
        articles = persistentListOf(),
        selectedCategory = ArticleCategory.ALL,
    )
}
