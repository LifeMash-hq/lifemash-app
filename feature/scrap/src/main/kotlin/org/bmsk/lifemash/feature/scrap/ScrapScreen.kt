package org.bmsk.lifemash.feature.scrap

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import org.bmsk.lifemash.core.designsystem.theme.LifeMashTheme
import org.bmsk.lifemash.core.model.DateParser
import org.bmsk.lifemash.core.model.NewsModel
import org.bmsk.lifemash.feature.scrap.component.ScrapNewsItem
import org.bmsk.lifemash.feature.scrap.component.rememberScrapNewsItemState

@Composable
internal fun ScrapScreen(scrapNewsList: PersistentList<NewsModel>) {
    val scrapNewsItemState = rememberScrapNewsItemState()
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(scrapNewsList) { news ->
            ScrapNewsItem(
                newsModel = news,
                state = scrapNewsItemState,
                onClick = {},
                onClickDelete = {},
            )
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun ScrapScreenPreview() {
    LifeMashTheme {
        ScrapScreen(
            scrapNewsList =
                persistentListOf(
                    NewsModel(
                        title = "NewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNews",
                        link = "",
                        pubDate = DateParser.parseDate("2024-06-06"),
                        imageUrl = "",
                    ),
                    NewsModel(
                        title = "NewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNews",
                        link = "",
                        pubDate = DateParser.parseDate("2024-06-06"),
                        imageUrl = "",
                    ),
                    NewsModel(
                        title = "NewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNews",
                        link = "",
                        pubDate = DateParser.parseDate("2024-06-06"),
                        imageUrl = "",
                    ),
                    NewsModel(
                        title = "NewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNews",
                        link = "",
                        pubDate = DateParser.parseDate("2024-06-06"),
                        imageUrl = "",
                    ),
                    NewsModel(
                        title = "NewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNews",
                        link = "",
                        pubDate = DateParser.parseDate("2024-06-06"),
                        imageUrl = "",
                    ),
                    NewsModel(
                        title = "NewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNews",
                        link = "",
                        pubDate = DateParser.parseDate("2024-06-06"),
                        imageUrl = "",
                    ),
                    NewsModel(
                        title = "NewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNews",
                        link = "",
                        pubDate = DateParser.parseDate("2024-06-06"),
                        imageUrl = "",
                    ),
                ),
        )
    }
}
