package org.bmsk.lifemash.feed.ui

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.bmsk.lifemash.feature.designsystem.component.AvatarSize
import org.bmsk.lifemash.feature.designsystem.component.LifeMashAvatar
import org.bmsk.lifemash.feature.designsystem.component.LifeMashButton
import org.bmsk.lifemash.feature.designsystem.component.LifeMashSkeleton
import org.bmsk.lifemash.feature.designsystem.component.NetworkImage
import org.bmsk.lifemash.feed.domain.model.FeedPost

@Composable
fun FeedScreen(
    uiState: FeedUiState,
    onRetry: () -> Unit = {},
    onFindFriends: () -> Unit = {},
    onPostClick: (String) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Box(modifier.fillMaxSize().statusBarsPadding()) {
        when (uiState) {
            is FeedUiState.Loading -> FeedSkeletonList()
            is FeedUiState.Empty -> {
                Column(
                    Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text("아직 피드가 없어요", style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.height(16.dp))
                    LifeMashButton(text = "친구 찾기", onClick = onFindFriends)
                }
            }
            is FeedUiState.Error -> {
                Column(
                    Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(uiState.message, style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.height(16.dp))
                    LifeMashButton(text = "재시도", onClick = onRetry)
                }
            }
            is FeedUiState.Loaded -> {
                LazyColumn(Modifier.fillMaxSize()) {
                    item {
                        FeedHeader(followingCount = uiState.followingCount)
                    }
                    items(uiState.posts, key = { it.id }) { post ->
                        FeedCard(post = post, onPostClick = onPostClick)
                    }
                }
            }
        }
    }
}

@Composable
private fun FeedHeader(followingCount: Int) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
        Text(
            text = "피드",
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            ),
        )
        if (followingCount > 0) {
            Text(
                text = "팔로잉 $followingCount 명",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun FeedCard(post: FeedPost, onPostClick: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
    ) {
        // Image with event tag overlay
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
        ) {
            NetworkImage(
                imageUrl = post.imageUrl,
                modifier = Modifier.fillMaxSize(),
            )
            // Event tag overlay at bottom
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.5f)),
                        ),
                    )
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                contentAlignment = Alignment.BottomStart,
            ) {
                Column {
                    Text(
                        text = post.eventTitle,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White,
                    )
                    post.eventDate?.let { date ->
                        Text(
                            text = date,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.85f),
                        )
                    }
                }
            }
        }

        // Bottom info
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
            // Author row
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                LifeMashAvatar(
                    imageUrl = post.authorProfileImage,
                    name = post.authorNickname,
                    size = AvatarSize.Small,
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = post.authorNickname,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = post.createdAt,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            // Caption
            post.caption?.let { caption ->
                Spacer(Modifier.height(4.dp))
                Text(
                    text = caption,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            // Preview comments
            if (post.previewComments.isNotEmpty()) {
                Spacer(Modifier.height(4.dp))
                post.previewComments.take(2).forEach { comment ->
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(comment.authorNickname)
                            }
                            append("  ")
                            append(comment.content)
                        },
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            // Comment prompt
            Spacer(Modifier.height(6.dp))
            Text(
                text = "댓글 달기...",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            )
        }
    }
}

@Composable
private fun FeedSkeletonList() {
    Column(modifier = Modifier.fillMaxSize().padding(top = 16.dp)) {
        repeat(3) {
            FeedCardSkeleton()
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun FeedCardSkeleton() {
    Column(modifier = Modifier.fillMaxWidth()) {
        LifeMashSkeleton(modifier = Modifier.fillMaxWidth().aspectRatio(1f), height = 0.dp)
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                            shape = androidx.compose.foundation.shape.CircleShape,
                        ),
                )
                Spacer(Modifier.width(8.dp))
                LifeMashSkeleton(modifier = Modifier.width(120.dp), height = 14.dp)
            }
            Spacer(Modifier.height(6.dp))
            LifeMashSkeleton(modifier = Modifier.fillMaxWidth(0.9f), height = 12.dp)
            Spacer(Modifier.height(4.dp))
            LifeMashSkeleton(modifier = Modifier.fillMaxWidth(0.7f), height = 12.dp)
        }
    }
}
