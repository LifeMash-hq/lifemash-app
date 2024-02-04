package org.bmsk.feature.topic

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bmsk.core.designsystem.component.LifeMashCard
import org.bmsk.core.designsystem.component.NetworkImage
import org.bmsk.core.designsystem.theme.LifeMashTheme
import org.bmsk.core.model.NewsModel
import org.bmsk.lifemash.feature.topic.R
import org.jsoup.Jsoup

@Composable
internal fun NewsCard(
    newsModel: NewsModel,
    onClick: () -> Unit,
) {
    var imageUrl by remember { mutableStateOf(newsModel.imageUrl) }
    LaunchedEffect(true) {
        if (imageUrl == null) {
            withContext(Dispatchers.IO) {
                val document = Jsoup.connect(newsModel.link).get()
                imageUrl = document.select("meta[property=og:image]").attr("content")
            }
        }
    }
    LifeMashCard(modifier = Modifier.clickable { onClick() }) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            NetworkImage(
                modifier = Modifier.fillMaxWidth().aspectRatio(2f),
                imageUrl = imageUrl,
            )
            Column(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = newsModel.title,
                    style = MaterialTheme.typography.titleMedium,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = newsModel.pubDate,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.baseline_more_vert_24),
                        contentDescription = null,
                    )
                }
            }
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
private fun NewsCardPreview() {
    val fakeNewsModel = NewsModel(title = "News Card", link = "", pubDate = "2023-08-20")
    LifeMashTheme {
        NewsCard(
            newsModel = fakeNewsModel,
            onClick = {},
        )
    }
}
