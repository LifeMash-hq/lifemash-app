@file:OptIn(kotlin.time.ExperimentalTime::class)
package org.bmsk.lifemash.eventdetail.ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.bmsk.lifemash.designsystem.theme.LifeMashTheme
import kotlin.time.Instant

@Preview(name = "Light - Loaded", showBackground = true)
@Preview(name = "Dark - Loaded", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun EventDetailScreenPreview_Loaded() {
    LifeMashTheme {
        EventDetailScreen(
            uiState = EventDetailUiState.Loaded(
                eventId = "event-1",
                title = "팀 스프린트 킥오프",
                date = "2024년 3월 15일 (금) 오전 10:00",
                startAt = Instant.fromEpochMilliseconds(0L),
                location = "강남구 테헤란로 123, 3층 회의실",
                description = "새 스프린트를 시작하며 목표와 역할을 정합니다. 모든 팀원 참석 부탁드립니다.",
                imageEmoji = "🚀",
                sharedByNickname = "bmsk",
                attendees = listOf(
                    Attendee(id = "u1", nickname = "bmsk"),
                    Attendee(id = "u2", nickname = "lifemash"),
                ),
                comments = listOf(
                    Comment(
                        id = "c1",
                        authorNickname = "bmsk",
                        content = "참석 확정!",
                        createdAt = "1시간 전",
                    ),
                    Comment(
                        id = "c2",
                        authorNickname = "lifemash",
                        content = "저도 갑니다 🙌",
                        createdAt = "30분 전",
                    ),
                ),
                isJoined = true,
            ),
        )
    }
}

@Preview(name = "Light - Loading", showBackground = true)
@Preview(name = "Dark - Loading", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun EventDetailScreenPreview_Loading() {
    LifeMashTheme {
        EventDetailScreen(uiState = EventDetailUiState.Loading)
    }
}
