package org.bmsk.lifemash.profile.ui

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.bmsk.lifemash.feature.designsystem.component.AvatarSize
import org.bmsk.lifemash.feature.designsystem.component.LifeMashAvatar
import org.bmsk.lifemash.feature.designsystem.component.LifeMashButton
import org.bmsk.lifemash.profile.domain.model.ProfileEvent
import org.bmsk.lifemash.profile.domain.model.UserProfile

@Composable
fun MyProfileScreen(
    uiState: ProfileUiState,
    onEditClick: () -> Unit = {},
    onFollowerClick: () -> Unit = {},
    onFollowingClick: () -> Unit = {},
    onMomentClick: (String) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Box(modifier.fillMaxSize().statusBarsPadding()) {
        when (uiState) {
            is ProfileUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is ProfileUiState.Error -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(uiState.message)
                }
            }
            is ProfileUiState.Loaded -> {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item {
                        ProfileHeader(
                            profile = uiState.profile,
                            onEditClick = onEditClick,
                            onFollowerClick = onFollowerClick,
                            onFollowingClick = onFollowingClick,
                        )
                    }
                    item {
                        MiniCalendarSection(
                            year = uiState.selectedYear,
                            month = uiState.selectedMonth,
                            eventDays = uiState.calendarEventDates,
                        )
                    }
                    item {
                        TodayEventsSection(events = uiState.todayEvents)
                    }
                    items(uiState.moments, key = { it.id }) { moment ->
                        Text(
                            text = moment.caption ?: moment.imageUrl,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileHeader(
    profile: UserProfile,
    onEditClick: () -> Unit,
    onFollowerClick: () -> Unit,
    onFollowingClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            LifeMashAvatar(
                name = profile.nickname,
                imageUrl = profile.profileImage,
                size = AvatarSize.Large,
            )
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    text = profile.nickname,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                )
                val bio = profile.bio
                if (bio != null) {
                    Text(bio, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            TextButton(onClick = onFollowerClick) {
                Text("팔로워 ${profile.followerCount}")
            }
            TextButton(onClick = onFollowingClick) {
                Text("팔로잉 ${profile.followingCount}")
            }
        }
        LifeMashButton(text = "편집", onClick = onEditClick, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun MiniCalendarSection(year: Int, month: Int, eventDays: Set<Int>) {
    val monthNames = listOf("1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월")
    val dayLabels = listOf("일", "월", "화", "수", "목", "금", "토")

    // Calculate first day of month and total days
    val daysInMonth = daysInMonth(year, month)
    val firstDayOfWeek = firstDayOfWeek(year, month) // 0=Sun

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 8.dp),
    ) {
        Text(
            text = "${year}년 ${if (month in 1..12) monthNames[month - 1] else ""}",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 8.dp),
        )
        // Day headers
        Row(modifier = Modifier.fillMaxWidth()) {
            dayLabels.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Spacer(Modifier.height(4.dp))
        // Calendar grid
        val totalCells = firstDayOfWeek + daysInMonth
        val rows = (totalCells + 6) / 7
        var dayCounter = 1
        repeat(rows) { row ->
            Row(modifier = Modifier.fillMaxWidth()) {
                repeat(7) { col ->
                    val cellIndex = row * 7 + col
                    Box(
                        modifier = Modifier.weight(1f).aspectRatio(1f),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (cellIndex >= firstDayOfWeek && dayCounter <= daysInMonth) {
                            val day = dayCounter
                            val hasEvent = day in eventDays
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = day.toString(),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 11.sp,
                                )
                                if (hasEvent) {
                                    Box(
                                        modifier = Modifier
                                            .size(4.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary),
                                    )
                                }
                            }
                            dayCounter++
                        }
                    }
                }
                if (dayCounter > daysInMonth) return@Row
            }
        }
    }
}

@Composable
private fun TodayEventsSection(events: List<ProfileEvent>) {
    if (events.isEmpty()) return
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 12.dp),
    ) {
        Text(
            text = "오늘 일정",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 8.dp),
        )
        events.forEach { event ->
            ProfileEventItem(event = event)
            Spacer(Modifier.height(6.dp))
        }
    }
}

@Composable
private fun ProfileEventItem(event: ProfileEvent) {
    val barColor = runCatching {
        val hex = event.color.trimStart('#')
        val r = hex.substring(0, 2).toInt(16)
        val g = hex.substring(2, 4).toInt(16)
        val b = hex.substring(4, 6).toInt(16)
        Color(r, g, b)
    }.getOrElse { MaterialTheme.colorScheme.primary }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(36.dp)
                .background(barColor),
        )
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = event.title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            )
            Text(
                text = "${event.startTime} – ${event.endTime}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private fun daysInMonth(year: Int, month: Int): Int {
    return when (month) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        2 -> if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) 29 else 28
        else -> 30
    }
}

private fun firstDayOfWeek(year: Int, month: Int): Int {
    // Tomohiko Sakamoto's algorithm (0=Sunday)
    val y = if (month < 3) year - 1 else year
    val m = if (month < 3) month + 10 else month - 2
    val c = y / 100
    val yy = y % 100
    return ((yy + yy / 4 + c / 4 - 2 * c + 26 * (m + 1) / 10 + 1 - 1) % 7 + 7) % 7
}
