package org.bmsk.lifemash.feed.ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.bmsk.lifemash.designsystem.theme.LifeMashTheme
import org.bmsk.lifemash.feed.domain.model.FeedComment
import org.bmsk.lifemash.feed.domain.model.FeedFilter
import org.bmsk.lifemash.feed.domain.model.FeedMedia
import org.bmsk.lifemash.feed.domain.model.FeedPost

private val samplePosts = listOf(
    FeedPost(
        id = "p1",
        authorId = "u1",
        authorNickname = "bmsk",
        eventId = "e1",
        eventTitle = "팀 스프린트 킥오프",
        eventDate = "2024.03.15",
        media = listOf(FeedMedia(mediaUrl = "", mediaType = "image", sortOrder = 0)),
        caption = "새 스프린트 시작! 열심히 달려봅시다 🚀",
        previewComments = listOf(
            FeedComment(authorNickname = "lifemash", content = "화이팅!"),
        ),
        likeCount = 12,
        isLiked = true,
        commentCount = 3,
        createdAt = "1시간 전",
    ),
    FeedPost(
        id = "p2",
        authorId = "u2",
        authorNickname = "runner",
        eventId = "e2",
        eventTitle = "마라톤 대회",
        eventDate = "2024.03.20",
        media = listOf(FeedMedia(mediaUrl = "", mediaType = "image", sortOrder = 0)),
        caption = "드디어 완주! 🏃‍♂️",
        previewComments = emptyList(),
        likeCount = 34,
        isLiked = false,
        commentCount = 8,
        createdAt = "3시간 전",
    ),
)

@Preview(name = "Light - Loaded", showBackground = true)
@Preview(name = "Dark - Loaded", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun FeedScreenPreview_Loaded() {
    LifeMashTheme {
        FeedScreen(
            uiState = FeedUiState.Loaded(
                posts = samplePosts,
                followingCount = 5,
            ),
            selectedFilter = FeedFilter.ALL,
            onFilterSelect = {},
        )
    }
}

@Preview(name = "Light - Loading", showBackground = true)
@Preview(name = "Dark - Loading", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun FeedScreenPreview_Loading() {
    LifeMashTheme {
        FeedScreen(
            uiState = FeedUiState.Loading,
            selectedFilter = FeedFilter.ALL,
            onFilterSelect = {},
        )
    }
}

@Preview(name = "Light - Empty", showBackground = true)
@Preview(name = "Dark - Empty", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun FeedScreenPreview_Empty() {
    LifeMashTheme {
        FeedScreen(
            uiState = FeedUiState.Empty,
            selectedFilter = FeedFilter.ALL,
            onFilterSelect = {},
        )
    }
}
