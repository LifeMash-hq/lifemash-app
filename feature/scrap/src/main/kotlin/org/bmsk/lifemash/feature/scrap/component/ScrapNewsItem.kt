package org.bmsk.lifemash.feature.scrap.component

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
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
import org.bmsk.lifemash.core.model.DateParser
import org.bmsk.lifemash.core.model.NewsModel
import org.bmsk.lifemash.feature.scrap.R

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun ScrapNewsItem(
    modifier: Modifier = Modifier,
    newsModel: NewsModel,
    state: ScrapNewsItemState,
    onClick: () -> Unit,
    onClickDelete: () -> Unit,
) {
    LifeMashCard(modifier = modifier) {
        val weightTarget = if (state.isLongClicked) 0.3f else 0.001f
        val weight by animateFloatAsState(targetValue = weightTarget, label = "")

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16 / 5f)
                .padding(8.dp)
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = state::onLongClick,
                ),
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
                        text = DateParser.formatDate(newsModel.pubDate),
                    )
                }
            }

            AnimatedVisibility(
                modifier = Modifier.weight(weight),
                visible = state.isLongClicked,
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
                            .clickable { state.onLongClick() },
                    )
                }
            }
        }
    }
}

internal class ScrapNewsItemState(
    initialIsLongClicked: Boolean = false,
) {
    var isLongClicked by mutableStateOf(initialIsLongClicked)
        private set

    fun onLongClick() {
        isLongClicked = isLongClicked.not()
    }
}

@Composable
internal fun rememberScrapNewsItemState(initialIsLongClicked: Boolean = false): ScrapNewsItemState =
    remember {
        ScrapNewsItemState(initialIsLongClicked)
    }

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun ScrapNewsItemPreview() {
    val fakeNews =
        NewsModel(
            title =
                "NewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNews" +
                        "NewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNewsNews",
            link = "",
            pubDate = DateParser.parseDate("Wed, 21 Jun 2023 12:34:56 GMT"),
            imageUrl = "",
        )

    LifeMashTheme {
        ScrapNewsItem(
            modifier = Modifier,
            newsModel = fakeNews,
            state = rememberScrapNewsItemState(),
            onClick = {},
            onClickDelete = {},
        )
    }
}
