package org.bmsk.lifemash.feature.scrap.component

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.bmsk.lifemash.core.designsystem.component.LifeMashCard
import org.bmsk.lifemash.core.designsystem.component.NetworkImage
import org.bmsk.lifemash.core.designsystem.theme.LifeMashTheme
import org.bmsk.lifemash.core.model.NewsModel
import org.bmsk.lifemash.feature.scrap.R

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun ScrapNewsItem(
    modifier: Modifier = Modifier,
    newsModel: NewsModel,
    onClick: () -> Unit,
    onClickDelete: () -> Unit,
) {
    LifeMashCard(modifier = modifier) {
        var longClicked by remember { mutableStateOf(false) }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16 / 5f)
                .padding(8.dp)
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = { longClicked = longClicked.not() }
                )
        ) {
            NetworkImage(
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1 / 1f),
                imageUrl = newsModel.imageUrl,
            )
            Column(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .fillMaxHeight()
                    .weight(1f),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = newsModel.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = newsModel.pubDate,
                    )
                }
            }

            AnimatedVisibility(
                modifier = Modifier.weight(.3f),
                visible = longClicked,
                enter = slideInHorizontally(),
                exit = slideOutHorizontally(targetOffsetX = { it }),
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_baseline_delete_24),
                        contentDescription = null,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onClickDelete() },
                    )
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_baseline_cancel_presentation_24),
                        contentDescription = null,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { longClicked = false },
                    )
                }
            }
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun ScrapNewsItemPreview() {
    val fakeNews = NewsModel(
        title = "NewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNews",
        link = "",
        pubDate = "2024-06-06",
        imageUrl = ""
    )
    LifeMashTheme {
        ScrapNewsItem(
            modifier = Modifier,
            newsModel = fakeNews,
            {},
            {},
        )
    }
}