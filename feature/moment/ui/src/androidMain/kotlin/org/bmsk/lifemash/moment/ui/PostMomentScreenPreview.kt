package org.bmsk.lifemash.moment.ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.bmsk.lifemash.designsystem.theme.LifeMashTheme
import org.bmsk.lifemash.moment.domain.model.MediaType
import org.bmsk.lifemash.moment.domain.model.Visibility

private val sampleFormEmpty = PostMomentFormState()

private val sampleFormWithContent = PostMomentFormState(
    caption = "오늘 팀 스프린트 킥오프! 함께 달려봅시다 🚀",
    visibility = Visibility.PUBLIC,
    eventId = "e1",
    eventTitle = "팀 스프린트 킥오프",
    media = listOf(
        SelectedMedia(id = "m1", localUri = "", mediaType = MediaType.IMAGE),
        SelectedMedia(id = "m2", localUri = "", mediaType = MediaType.IMAGE),
    ),
)

private val sampleFormFollowers = PostMomentFormState(
    caption = "팔로워 전용 순간 기록",
    visibility = Visibility.FOLLOWERS,
    eventTitle = "마라톤 대회",
)

@Preview(name = "Light - Idle (Empty)", showBackground = true)
@Preview(name = "Dark - Idle (Empty)", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PostMomentScreenPreview_Idle_Empty() {
    LifeMashTheme {
        PostMomentScreen(
            form = sampleFormEmpty,
            uiState = PostMomentUiState.Idle,
            onCaptionChange = {},
            onCycleVisibility = {},
            onTagEventClick = {},
            onAddMedia = {},
            onRemoveMedia = {},
            onSubmit = {},
            onClose = {},
        )
    }
}

@Preview(name = "Light - Idle (With Content)", showBackground = true)
@Preview(name = "Dark - Idle (With Content)", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PostMomentScreenPreview_Idle_WithContent() {
    LifeMashTheme {
        PostMomentScreen(
            form = sampleFormWithContent,
            uiState = PostMomentUiState.Idle,
            onCaptionChange = {},
            onCycleVisibility = {},
            onTagEventClick = {},
            onAddMedia = {},
            onRemoveMedia = {},
            onSubmit = {},
            onClose = {},
        )
    }
}

@Preview(name = "Light - Uploading", showBackground = true)
@Preview(name = "Dark - Uploading", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PostMomentScreenPreview_Uploading() {
    LifeMashTheme {
        PostMomentScreen(
            form = sampleFormWithContent,
            uiState = PostMomentUiState.Uploading(progress = 0.6f),
            onCaptionChange = {},
            onCycleVisibility = {},
            onTagEventClick = {},
            onAddMedia = {},
            onRemoveMedia = {},
            onSubmit = {},
            onClose = {},
        )
    }
}

@Preview(name = "Light - Followers Visibility", showBackground = true)
@Preview(name = "Dark - Followers Visibility", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PostMomentScreenPreview_FollowersVisibility() {
    LifeMashTheme {
        PostMomentScreen(
            form = sampleFormFollowers,
            uiState = PostMomentUiState.Idle,
            onCaptionChange = {},
            onCycleVisibility = {},
            onTagEventClick = {},
            onAddMedia = {},
            onRemoveMedia = {},
            onSubmit = {},
            onClose = {},
        )
    }
}
