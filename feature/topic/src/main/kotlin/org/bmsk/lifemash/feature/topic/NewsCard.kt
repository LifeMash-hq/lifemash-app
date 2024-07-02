package org.bmsk.lifemash.feature.topic

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.bmsk.lifemash.core.designsystem.component.LifeMashCard
import org.bmsk.lifemash.core.designsystem.component.NetworkImage
import org.bmsk.lifemash.core.designsystem.effect.noRippleClickable
import org.bmsk.lifemash.core.designsystem.theme.LifeMashTheme
import org.bmsk.lifemash.core.model.DateParser
import org.bmsk.lifemash.core.model.NewsModel

@Composable
internal fun NewsCard(
    modifier: Modifier = Modifier,
    newsModel: NewsModel,
    onClick: () -> Unit,
    onClickMore: () -> Unit,
) {
    LifeMashCard(modifier = modifier.clickable { onClick() }) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            NetworkImage(
                modifier = Modifier.fillMaxWidth().aspectRatio(2f),
                imageUrl = newsModel.imageUrl,
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
                        text = DateParser.formatDate(newsModel.pubDate),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Box(
                        modifier = Modifier
                            .noRippleClickable { onClickMore() }
                            .padding(4.dp)
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.baseline_more_vert_24),
                            contentDescription = null,
                        )
                    }
                }
            }
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
private fun NewsCardPreview() {
    val fakeNewsModel = NewsModel(title = "News Card", link = "", pubDate = DateParser.parseDate("2023-08-20"))
    LifeMashTheme {
        NewsCard(
            newsModel = fakeNewsModel,
            onClick = {},
            onClickMore = {},
        )
    }
}
