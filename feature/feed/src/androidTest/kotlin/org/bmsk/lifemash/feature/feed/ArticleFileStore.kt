package org.bmsk.lifemash.feature.feed

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bmsk.lifemash.core.repo.article.api.ArticleCategory
import java.io.File
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ArticleFileStore @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private fun fileFor(category: ArticleCategory): File =
        File(context.filesDir, "articles_${category.name.lowercase()}.txt")

    suspend fun save(category: ArticleCategory, articles: List<ArticleUi>) =
        withContext(Dispatchers.IO) {
            val sb = StringBuilder()
            articles.forEachIndexed { idx, a ->
                sb.appendLine("===== Article ${idx + 1} =====")
                sb.appendLine("id: ${a.id.oneLine()}")
                sb.appendLine("publisher: ${a.publisher.oneLine()}")
                sb.appendLine("title: ${a.title.oneLine()}")
                sb.appendLine("summary: ${a.summary.oneLine()}")
                sb.appendLine("link: ${a.link.oneLine()}")
                sb.appendLine("image: ${a.image?.oneLine() ?: "null"}")
                sb.appendLine("publishedAt: ${formatEpoch(a.publishedAt)} (raw=${a.publishedAt})")
                sb.appendLine("host: ${a.host.oneLine()}")
                sb.appendLine("categories: ${a.categories.joinToString(",") { it.name }}")
                sb.appendLine()
            }
            fileFor(category).writeText(sb.toString())
        }

    /** 디버깅용: 역직렬화 없이 원문 텍스트를 그대로 읽음 */
    suspend fun loadRaw(category: ArticleCategory): String = withContext(Dispatchers.IO) {
        val f = fileFor(category)
        if (f.exists()) f.readText() else ""
    }

    private fun String.oneLine(): String =
        this.replace('\n', ' ').replace('\r', ' ').replace('\t', ' ').trim()

    private fun formatEpoch(epoch: Long): String {
        // epoch가 seconds일 가능성 방지: 10자리면 seconds로 간주
        val millis = if (epoch in 1_000_000_000L..9_999_999_999L) epoch * 1000 else epoch
        val zdt = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault())
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z").format(zdt)
    }
}