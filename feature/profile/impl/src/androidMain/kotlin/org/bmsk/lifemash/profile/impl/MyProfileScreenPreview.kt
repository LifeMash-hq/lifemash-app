package org.bmsk.lifemash.profile.impl

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.bmsk.lifemash.designsystem.theme.LifeMashTheme
import org.bmsk.lifemash.domain.moment.Moment
import org.bmsk.lifemash.domain.moment.Visibility
import org.bmsk.lifemash.domain.profile.ProfileEvent
import org.bmsk.lifemash.domain.profile.UserProfile

private val sampleProfile = UserProfile(
    id = "u1",
    email = "bmsk@lifemash.app",
    nickname = "bmsk",
    bio = "캘린더 기반 소셜 앱 개발 중",
    followerCount = 128,
    followingCount = 64,
    isFollowing = false,
)

private val sampleMoments = listOf(
    Moment(
        id = "m1",
        eventId = "e1",
        authorId = "u1",
        authorNickname = "bmsk",
        media = emptyList(),
        caption = "스프린트 완료!",
        visibility = Visibility.PUBLIC,
        createdAt = "2024-03-15",
    ),
)

private val sampleTodayEvents = listOf(
    ProfileEvent(
        id = "e1",
        title = "팀 스탠드업",
        startTime = "10:00",
        endTime = "10:15",
        color = "#4F6AF5",
    ),
)

@Preview(name = "Light - Loaded", showBackground = true)
@Preview(name = "Dark - Loaded", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun MyProfileScreenPreview_Loaded() {
    LifeMashTheme {
        MyProfileScreen(
            uiState = ProfileUiState.Default.copy(
                screenPhase = ScreenPhase.Ready,
                profile = sampleProfile,
                moments = sampleMoments,
                todayEvents = sampleTodayEvents,
                selectedYear = 2024,
                selectedMonth = 3,
            ),
        )
    }
}

@Preview(name = "Light - Loading", showBackground = true)
@Preview(name = "Dark - Loading", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun MyProfileScreenPreview_Loading() {
    LifeMashTheme {
        MyProfileScreen(uiState = ProfileUiState.Default)
    }
}
