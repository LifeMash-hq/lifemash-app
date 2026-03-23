package org.bmsk.lifemash.home.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.bmsk.lifemash.feature.designsystem.theme.LifeMashTheme
import org.bmsk.lifemash.home.api.BlockGroup
import org.bmsk.lifemash.home.api.BlocksTodayData
import org.bmsk.lifemash.home.api.HomeBlock
import org.bmsk.lifemash.home.api.TodayEvent

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    val blocks = listOf(
        HomeBlock.CalendarToday(visible = true),
        HomeBlock.Groups(visible = true),
        HomeBlock.Assistant(visible = true),
    )
    val todayData = BlocksTodayData(
        todayEvents = listOf(
            TodayEvent(id = "1", title = "팀 미팅", startTime = "10:00", allDay = false),
            TodayEvent(id = "2", title = "점심 약속", startTime = "12:30", allDay = false),
            TodayEvent(id = "3", title = "프로젝트 마감", startTime = "", allDay = true),
        ),
        groups = listOf(
            BlockGroup(id = "1", name = "개발팀", memberCount = 5, latestActivity = null),
            BlockGroup(id = "2", name = "디자인팀", memberCount = 3, latestActivity = null),
        ),
    )

    LifeMashTheme {
        HomeScreen(
            blocks = blocks,
            todayData = todayData,
            accessToken = null,
            onNavigateToBlockSettings = {},
            onNavigateToAssistant = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenEmptyPreview() {
    LifeMashTheme {
        HomeScreen(
            blocks = listOf(
                HomeBlock.CalendarToday(visible = true),
                HomeBlock.Groups(visible = true),
                HomeBlock.Assistant(visible = true),
            ),
            todayData = null,
            accessToken = null,
            onNavigateToBlockSettings = {},
            onNavigateToAssistant = {},
        )
    }
}
