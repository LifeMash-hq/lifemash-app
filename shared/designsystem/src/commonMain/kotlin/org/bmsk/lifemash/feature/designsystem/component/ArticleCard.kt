package org.bmsk.lifemash.feature.designsystem.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.bmsk.lifemash.feature.designsystem.theme.BgCard
import org.bmsk.lifemash.feature.designsystem.theme.LifeMashRadius
import org.bmsk.lifemash.feature.designsystem.theme.LifeMashSpacing
import org.bmsk.lifemash.feature.designsystem.theme.TextSecondary

sealed class ArticleCardVariant {
    data object Standard : ArticleCardVariant()
    data object Compact : ArticleCardVariant()
    data object Headline : ArticleCardVariant()
}

@Composable
fun ArticleCard(
    title: String,
    source: String,
    publishedAt: String,
    modifier: Modifier = Modifier,
    imageUrl: String? = null,
    category: ArticleCategory? = null,
    isScrapped: Boolean = false,
    variant: ArticleCardVariant = ArticleCardVariant.Standard,
    onClick: () -> Unit = {},
    onScrapClick: () -> Unit = {},
) {
    when (variant) {
        ArticleCardVariant.Standard -> StandardArticleCard(
            title, source, publishedAt, imageUrl, category, isScrapped, onClick, onScrapClick, modifier,
        )
        ArticleCardVariant.Compact -> CompactArticleCard(
            title, source, publishedAt, imageUrl, category, isScrapped, onClick, onScrapClick, modifier,
        )
        ArticleCardVariant.Headline -> HeadlineArticleCard(
            title, source, publishedAt, imageUrl, category, isScrapped, onClick, onScrapClick, modifier,
        )
    }
}

@Composable
private fun StandardArticleCard(
    title: String, source: String, publishedAt: String, imageUrl: String?,
    category: ArticleCategory?, isScrapped: Boolean,
    onClick: () -> Unit, onScrapClick: () -> Unit, modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth().clip(RoundedCornerShape(LifeMashRadius.md)).clickable(onClick = onClick),
        color = BgCard, shape = RoundedCornerShape(LifeMashRadius.md), shadowElevation = 1.dp,
    ) {
        Column {
            if (imageUrl != null) {
                NetworkImage(
                    imageUrl = imageUrl, contentDescription = title, contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(topStart = LifeMashRadius.md, topEnd = LifeMashRadius.md)),
                )
            }
            Column(modifier = Modifier.padding(LifeMashSpacing.lg)) {
                category?.let {
                    CategoryBadge(category = it)
                    Spacer(Modifier.height(LifeMashSpacing.sm))
                }
                Text(text = title, style = MaterialTheme.typography.headlineSmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(LifeMashSpacing.sm))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "$source · $publishedAt", style = MaterialTheme.typography.labelLarge, color = TextSecondary)
                    ScrapButton(isScrapped = isScrapped, onClick = onScrapClick)
                }
            }
        }
    }
}

@Composable
private fun CompactArticleCard(
    title: String, source: String, publishedAt: String, imageUrl: String?,
    category: ArticleCategory?, isScrapped: Boolean,
    onClick: () -> Unit, onScrapClick: () -> Unit, modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth().clip(RoundedCornerShape(LifeMashRadius.md)).clickable(onClick = onClick),
        color = BgCard, shape = RoundedCornerShape(LifeMashRadius.md), shadowElevation = 1.dp,
    ) {
        Row(modifier = Modifier.padding(LifeMashSpacing.md), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f).padding(end = LifeMashSpacing.md)) {
                category?.let {
                    CategoryBadge(category = it)
                    Spacer(Modifier.height(LifeMashSpacing.xs))
                }
                Text(text = title, style = MaterialTheme.typography.bodyMedium, maxLines = 3, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(LifeMashSpacing.xs))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "$source · $publishedAt", style = MaterialTheme.typography.labelLarge, color = TextSecondary, modifier = Modifier.weight(1f))
                    ScrapButton(isScrapped = isScrapped, onClick = onScrapClick)
                }
            }
            if (imageUrl != null) {
                NetworkImage(imageUrl = imageUrl, contentDescription = title, contentScale = ContentScale.Crop,
                    modifier = Modifier.size(80.dp).clip(RoundedCornerShape(LifeMashRadius.sm)))
            }
        }
    }
}

@Composable
private fun HeadlineArticleCard(
    title: String, source: String, publishedAt: String, imageUrl: String?,
    category: ArticleCategory?, isScrapped: Boolean,
    onClick: () -> Unit, onScrapClick: () -> Unit, modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth().clip(RoundedCornerShape(LifeMashRadius.lg)).clickable(onClick = onClick),
        color = BgCard, shape = RoundedCornerShape(LifeMashRadius.lg), shadowElevation = 2.dp,
    ) {
        Box {
            if (imageUrl != null) {
                NetworkImage(imageUrl = imageUrl, contentDescription = title, contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().aspectRatio(4f / 3f))
                Spacer(modifier = Modifier.matchParentSize().then(Modifier.fillMaxSize().align(Alignment.BottomStart)))
            }
            Column(modifier = Modifier.align(Alignment.BottomStart).fillMaxWidth().padding(LifeMashSpacing.lg)) {
                category?.let {
                    CategoryBadge(category = it)
                    Spacer(Modifier.height(LifeMashSpacing.sm))
                }
                Text(text = title, style = MaterialTheme.typography.headlineLarge,
                    color = if (imageUrl != null) Color.White else MaterialTheme.colorScheme.onSurface,
                    maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(LifeMashSpacing.sm))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "$source · $publishedAt", style = MaterialTheme.typography.labelLarge,
                        color = if (imageUrl != null) Color.White.copy(alpha = 0.8f) else TextSecondary)
                    ScrapButton(isScrapped = isScrapped, onClick = onScrapClick)
                }
            }
        }
    }
}
