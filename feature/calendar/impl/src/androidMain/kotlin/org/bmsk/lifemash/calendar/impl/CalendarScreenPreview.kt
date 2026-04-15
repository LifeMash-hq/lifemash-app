package org.bmsk.lifemash.calendar.impl

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import kotlin.time.Instant
import kotlinx.collections.immutable.persistentListOf
import kotlinx.datetime.LocalDate
import org.bmsk.lifemash.domain.calendar.Event
import org.bmsk.lifemash.domain.calendar.EventTiming
import org.bmsk.lifemash.domain.calendar.Group
import org.bmsk.lifemash.domain.calendar.GroupType
import org.bmsk.lifemash.designsystem.theme.LifeMashTheme

internal val sampleInstant = Instant.parse("2024-03-15T10:00:00Z")

internal val sampleEvents = persistentListOf(
    Event(
        id = "event-1",
        groupId = "group-1",
        authorId = "user-1",
        title = "팀 스프린트 회의",
        description = "2주 스프린트 계획 수립",
        location = null,
        timing = EventTiming.Timed(
            start = Instant.parse("2024-03-15T09:00:00Z"),
            end = Instant.parse("2024-03-15T10:00:00Z"),
        ),
        color = "#4F6AF5",
        createdAt = sampleInstant,
        updatedAt = sampleInstant,
    ),
    Event(
        id = "event-2",
        groupId = "group-1",
        authorId = "user-2",
        title = "가족 저녁 식사",
        description = null,
        location = null,
        timing = EventTiming.Timed(
            start = Instant.parse("2024-03-15T18:00:00Z"),
            end = Instant.parse("2024-03-15T20:00:00Z"),
        ),
        color = "#F5A623",
        createdAt = sampleInstant,
        updatedAt = sampleInstant,
    ),
)

private val sampleGroups = persistentListOf(
    Group(
        id = "group-1",
        name = "우리 팀",
        type = GroupType.TEAM,
        maxMembers = 10,
        inviteCode = "ABC123",
        members = emptyList(),
        createdAt = sampleInstant,
    ),
)

@Preview(name = "Light - Calendar", showBackground = true)
@Preview(name = "Dark - Calendar", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
internal fun CalendarScreenPreview_Calendar() {
    LifeMashTheme {
        CalendarScreen(
            uiState = CalendarUiState.Default.copy(
                isLoading = false,
                currentYear = 2024,
                currentMonth = 3,
                selectedDate = LocalDate(2024, 3, 15),
                events = sampleEvents,
                groups = sampleGroups,
                selectedGroup = sampleGroups.first(),
            ),
            onDateSelect = {},
            onChangeMonth = { _, _, _ -> },
            onSelectGroup = {},
            onShowOverlay = {},
            onCreateGroup = { _, _ -> },
            onJoinGroup = {},
        )
    }
}

@Preview(name = "Light - Loading", showBackground = true)
@Preview(name = "Dark - Loading", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
internal fun CalendarScreenPreview_Loading() {
    LifeMashTheme {
        CalendarScreen(
            uiState = CalendarUiState.Default,
            onDateSelect = {},
            onChangeMonth = { _, _, _ -> },
            onSelectGroup = {},
            onShowOverlay = {},
            onCreateGroup = { _, _ -> },
            onJoinGroup = {},
        )
    }
}

@Preview(name = "Light - Error", showBackground = true)
@Preview(name = "Dark - Error", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
internal fun CalendarScreenPreview_Error() {
    LifeMashTheme {
        CalendarScreen(
            uiState = CalendarUiState.Default.copy(
                isLoading = false,
                errorMessage = "네트워크 오류가 발생했습니다",
                currentYear = 2024,
                currentMonth = 3,
                selectedDate = LocalDate(2024, 3, 15),
                groups = sampleGroups,
                selectedGroup = sampleGroups.first(),
            ),
            onDateSelect = {},
            onChangeMonth = { _, _, _ -> },
            onSelectGroup = {},
            onShowOverlay = {},
            onCreateGroup = { _, _ -> },
            onJoinGroup = {},
        )
    }
}
