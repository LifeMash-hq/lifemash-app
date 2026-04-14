package org.bmsk.lifemash.profile.impl

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.bmsk.lifemash.designsystem.component.AvatarSize
import org.bmsk.lifemash.designsystem.component.LifeMashAvatar
import org.bmsk.lifemash.designsystem.component.LifeMashPrivacyLabel
import org.bmsk.lifemash.designsystem.component.PrivacyLevel
import org.bmsk.lifemash.designsystem.component.LifeMashButton
import org.bmsk.lifemash.designsystem.component.LifeMashButtonStyle
import org.bmsk.lifemash.designsystem.component.LifeMashProfileSubTabs
import org.bmsk.lifemash.designsystem.component.NetworkImage
import org.bmsk.lifemash.designsystem.theme.LifeMashShadow
import org.bmsk.lifemash.designsystem.theme.LifeMashSpacing
import org.bmsk.lifemash.domain.profile.CalendarDayEvent
import org.bmsk.lifemash.domain.profile.ProfileSubTab
import org.bmsk.lifemash.domain.profile.CalendarViewMode
import org.bmsk.lifemash.domain.moment.Moment
import org.bmsk.lifemash.domain.profile.ProfileEvent
import org.bmsk.lifemash.domain.profile.UserProfile
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun MyProfileScreen(
    uiState: ProfileUiState,
    onEditClick: () -> Unit = {},
    onFollowerClick: () -> Unit = {},
    onFollowingClick: () -> Unit = {},
    onMomentClick: (String) -> Unit = {},
    onSubTabSelect: (ProfileSubTab) -> Unit = {},
    onCalendarDaySelect: (Int?) -> Unit = {},
    onNavigateMonth: (Int) -> Unit = {},
    onNavigateToEventCreate: (year: Int, month: Int, day: Int) -> Unit = { _, _, _ -> },
    onCameraClick: (eventId: String) -> Unit = {},
    onEventClick: (eventId: String) -> Unit = {},
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
                Box(Modifier.fillMaxSize()) {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        item {
                            ProfileHeader(
                                profile = uiState.profile,
                                momentCount = uiState.moments.size,
                                selectedSubTab = uiState.selectedSubTab,
                                onFollowerClick = onFollowerClick,
                                onFollowingClick = onFollowingClick,
                                onSubTabSelect = onSubTabSelect,
                                actionContent = {
                                    LifeMashButton(
                                        text = "프로필 편집",
                                        onClick = onEditClick,
                                        modifier = Modifier.fillMaxWidth(),
                                        style = LifeMashButtonStyle.Outline,
                                    )
                                },
                            )
                        }
                        when (uiState.selectedSubTab) {
                            ProfileSubTab.MOMENTS -> {
                                item {
                                    PhotoGrid(
                                        moments = uiState.moments,
                                        onMomentClick = onMomentClick,
                                    )
                                }
                                item {
                                    UpcomingEventsSection(
                                        events = uiState.todayEvents,
                                        onEventClick = onEventClick,
                                    )
                                }
                            }
                            ProfileSubTab.CALENDAR -> {
                                item {
                                    CalendarSection(
                                        year = uiState.selectedYear,
                                        month = uiState.selectedMonth,
                                        calendarEvents = uiState.calendarEvents,
                                        selectedDay = uiState.selectedCalendarDay,
                                        viewMode = uiState.calendarViewMode,
                                        onDaySelect = onCalendarDaySelect,
                                        onPrevMonth = { onNavigateMonth(-1) },
                                        onNextMonth = { onNavigateMonth(1) },
                                    )
                                }
                                val selectedDay = uiState.selectedCalendarDay
                                val selectedDayEvents = if (selectedDay != null) {
                                    uiState.dayEvents[selectedDay] ?: emptyList()
                                } else {
                                    uiState.todayEvents
                                }
                                item {
                                    SelectedDayEventsSection(
                                        label = if (selectedDay != null) "${uiState.selectedMonth}월 ${selectedDay}일" else "오늘",
                                        events = selectedDayEvents,
                                        onCameraClick = onCameraClick,
                                        onEventClick = onEventClick,
                                    )
                                }
                                item { Spacer(Modifier.height(80.dp)) }
                            }
                        }
                    }

                    if (uiState.selectedSubTab == ProfileSubTab.CALENDAR) {
                        FloatingActionButton(
                            onClick = {
                                onNavigateToEventCreate(
                                    uiState.selectedYear,
                                    uiState.selectedMonth,
                                    uiState.selectedCalendarDay ?: 0,
                                )
                            },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(LifeMashSpacing.lg),
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = "일정 추가")
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun ProfileHeader(
    profile: UserProfile,
    momentCount: Int,
    selectedSubTab: ProfileSubTab,
    onFollowerClick: () -> Unit,
    onFollowingClick: () -> Unit,
    onSubTabSelect: (ProfileSubTab) -> Unit,
    actionContent: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = LifeMashSpacing.xxl),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LifeMashAvatar(
            name = profile.nickname,
            imageUrl = profile.profileImage,
            size = AvatarSize.XLarge,
        )
        Spacer(Modifier.height(LifeMashSpacing.md))
        Text(
            text = profile.nickname,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center,
        )
        val bio = profile.bio
        if (bio != null) {
            Spacer(Modifier.height(LifeMashSpacing.xxs))
            Text(
                text = bio,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
        Spacer(Modifier.height(LifeMashSpacing.lg))
        Row(
            horizontalArrangement = Arrangement.spacedBy(LifeMashSpacing.xxl),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ProfileStat(count = momentCount.toString(), label = "게시물")
            ProfileStat(
                count = formatStatCount(profile.followerCount),
                label = "팔로워",
                onClick = onFollowerClick,
            )
            ProfileStat(
                count = formatStatCount(profile.followingCount),
                label = "팔로잉",
                onClick = onFollowingClick,
            )
        }
        Spacer(Modifier.height(LifeMashSpacing.lg))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = LifeMashSpacing.xl),
        ) {
            actionContent()
        }
        Spacer(Modifier.height(LifeMashSpacing.xl))
        LifeMashProfileSubTabs(
            tabs = listOf("순간", "캘린더"),
            selectedIndex = when (selectedSubTab) {
                ProfileSubTab.MOMENTS -> 0
                ProfileSubTab.CALENDAR -> 1
            },
            onTabSelect = { index ->
                onSubTabSelect(if (index == 0) ProfileSubTab.MOMENTS else ProfileSubTab.CALENDAR)
            },
        )
    }
}

@Composable
internal fun ProfileStat(
    count: String,
    label: String,
    onClick: (() -> Unit)? = null,
) {
    val modifier = if (onClick != null) Modifier.clickable { onClick() } else Modifier
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = count,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

internal fun formatStatCount(count: Int): String {
    return when {
        count >= 10000 -> "${count / 1000 / 10.0}만"
        count >= 1000 -> "${count / 100 / 10.0}k"
        else -> count.toString()
    }
}

@Composable
internal fun PhotoGrid(
    moments: List<Moment>,
    onMomentClick: (String) -> Unit,
) {
    if (moments.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "아직 게시물이 없습니다",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        return
    }
    val rows = (moments.size + 2) / 3
    Column(modifier = Modifier.fillMaxWidth()) {
        repeat(rows) { rowIndex ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(LifeMashSpacing.micro),
            ) {
                repeat(3) { colIndex ->
                    val momentIndex = rowIndex * 3 + colIndex
                    val moment = moments.getOrNull(momentIndex)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .then(
                                if (moment != null) Modifier.clickable { onMomentClick(moment.id) }
                                else Modifier,
                            ),
                    ) {
                        if (moment != null) {
                            NetworkImage(
                                imageUrl = moment.media.firstOrNull()?.mediaUrl.orEmpty(),
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(LifeMashSpacing.micro))
        }
    }
}

@Composable
internal fun UpcomingEventsSection(
    events: List<ProfileEvent>,
    onEventClick: (eventId: String) -> Unit = {},
) {
    if (events.isEmpty()) return
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = LifeMashSpacing.xl)
            .padding(top = LifeMashSpacing.lg, bottom = LifeMashSpacing.md),
    ) {
        Text(
            text = "다가오는 이벤트",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = LifeMashSpacing.md),
        )
        events.forEach { event ->
            UpcomingEventItem(event = event, onClick = { onEventClick(event.id) })
            Spacer(Modifier.height(LifeMashSpacing.sm))
        }
    }
}

@Composable
internal fun UpcomingEventItem(event: ProfileEvent, onClick: () -> Unit = {}) {
    val barColor = parseColor(event.color, MaterialTheme.colorScheme.primary)
    Surface(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(LifeMashSpacing.md),
        shadowElevation = LifeMashShadow.sm,
        color = MaterialTheme.colorScheme.surface,
    ) {
        Row(
            modifier = Modifier.padding(LifeMashSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .width(LifeMashSpacing.xxs)
                    .height(36.dp)
                    .clip(RoundedCornerShape(LifeMashSpacing.micro))
                    .background(barColor),
            )
            Spacer(Modifier.width(LifeMashSpacing.md))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                )
                Text(
                    text = "${event.startTime} – ${event.endTime}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = LifeMashSpacing.micro),
                )
            }
            Text(
                text = visibilityLabel(event.visibility),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
internal fun CalendarSection(
    year: Int,
    month: Int,
    calendarEvents: Map<Int, List<CalendarDayEvent>>,
    selectedDay: Int?,
    viewMode: CalendarViewMode,
    onDaySelect: (Int?) -> Unit,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
) {
    val monthNames = listOf(
        "1월",
        "2월",
        "3월",
        "4월",
        "5월",
        "6월",
        "7월",
        "8월",
        "9월",
        "10월",
        "11월",
        "12월",
    )
    val dayLabels = listOf(
        "일",
        "월",
        "화",
        "수",
        "목",
        "금",
        "토",
    )
    val daysInMonth = daysInMonth(year, month)
    val firstDayOfWeek = firstDayOfWeek(year, month)
    val primary = MaterialTheme.colorScheme.primary
    val onPrimary = MaterialTheme.colorScheme.onPrimary

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = LifeMashSpacing.xl)
            .padding(top = LifeMashSpacing.md, bottom = LifeMashSpacing.sm),
    ) {
        // 월 헤더
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            IconButton(onClick = onPrevMonth, modifier = Modifier.size(28.dp)) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = "이전 달",
                    modifier = Modifier.size(LifeMashSpacing.md),
                )
            }
            Text(
                text = "${year}년 ${if (month in 1..12) monthNames[month - 1] else ""}",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            )
            IconButton(onClick = onNextMonth, modifier = Modifier.size(28.dp)) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "다음 달",
                    modifier = Modifier.size(LifeMashSpacing.md),
                )
            }
        }
        Spacer(Modifier.height(LifeMashSpacing.sm))
        // 요일 헤더
        Row(modifier = Modifier.fillMaxWidth()) {
            dayLabels.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                )
            }
        }
        Spacer(Modifier.height(LifeMashSpacing.xxs))
        // 달력 그리드
        val totalCells = firstDayOfWeek + daysInMonth
        val rows = (totalCells + 6) / 7
        var dayCounter = 1
        repeat(rows) { row ->
            Row(modifier = Modifier.fillMaxWidth()) {
                repeat(7) { col ->
                    val cellIndex = row * 7 + col
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (cellIndex >= firstDayOfWeek && dayCounter <= daysInMonth) {
                            val day = dayCounter
                            val events = calendarEvents[day] ?: emptyList()
                            val isSelected = day == selectedDay

                            if (viewMode == CalendarViewMode.CHIP) {
                                CalendarChipDayCell(
                                    day = day,
                                    events = events,
                                    isSelected = isSelected,
                                    onDaySelect = onDaySelect,
                                    primary = primary,
                                    onPrimary = onPrimary,
                                )
                            } else {
                                CalendarDotDayCell(
                                    day = day,
                                    events = events,
                                    isSelected = isSelected,
                                    onDaySelect = onDaySelect,
                                    primary = primary,
                                    onPrimary = onPrimary,
                                )
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
internal fun CalendarDotDayCell(
    day: Int,
    events: List<CalendarDayEvent>,
    isSelected: Boolean,
    onDaySelect: (Int?) -> Unit,
    primary: Color,
    onPrimary: Color,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { onDaySelect(if (isSelected) null else day) },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier.size(22.dp),
            contentAlignment = Alignment.Center,
        ) {
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(primary),
                )
            }
            Text(
                text = day.toString(),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                color = if (isSelected) onPrimary else MaterialTheme.colorScheme.onSurface,
            )
        }
        if (events.isNotEmpty()) {
            Spacer(Modifier.height(LifeMashSpacing.micro))
            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                events.take(3).forEach { event ->
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(parseColor(event.color, primary)),
                    )
                }
            }
        }
    }
}

@Composable
internal fun CalendarChipDayCell(
    day: Int,
    events: List<CalendarDayEvent>,
    isSelected: Boolean,
    onDaySelect: (Int?) -> Unit,
    primary: Color,
    onPrimary: Color,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(LifeMashSpacing.micro)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { onDaySelect(if (isSelected) null else day) },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier.size(22.dp),
            contentAlignment = Alignment.Center,
        ) {
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(primary),
                )
            }
            Text(
                text = day.toString(),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                color = if (isSelected) onPrimary else MaterialTheme.colorScheme.onSurface,
            )
        }
        events.take(3).forEach { event ->
            val chipColor = parseColor(event.color, primary)
            Text(
                text = event.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 1.dp)
                    .clip(RoundedCornerShape(LifeMashSpacing.micro))
                    .background(chipColor),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = MaterialTheme.typography.labelSmall.fontSize * 0.8f,
                ),
                color = onPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
internal fun SelectedDayEventsSection(
    label: String,
    events: List<ProfileEvent>,
    onCameraClick: (eventId: String) -> Unit,
    onEventClick: (eventId: String) -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = LifeMashSpacing.xl)
            .padding(top = LifeMashSpacing.md, bottom = LifeMashSpacing.md),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = LifeMashSpacing.sm),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            )
            Spacer(Modifier.width(LifeMashSpacing.sm))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(LifeMashSpacing.sm))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = LifeMashSpacing.sm, vertical = LifeMashSpacing.micro),
            ) {
                Text(
                    text = events.size.toString(),
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
        if (events.isEmpty()) {
            Text(
                text = "일정이 없습니다",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = LifeMashSpacing.lg),
            )
        } else {
            events.forEach { event ->
                DayEventItem(
                    event = event,
                    onCameraClick = onCameraClick,
                    onClick = { onEventClick(event.id) },
                )
                Spacer(Modifier.height(LifeMashSpacing.xs))
            }
        }
    }
}

@Composable
internal fun DayEventItem(
    event: ProfileEvent,
    onCameraClick: (eventId: String) -> Unit,
    onClick: () -> Unit = {},
) {
    val barColor = parseColor(event.color, MaterialTheme.colorScheme.primary)
    val privacyLevel = when (event.visibility) {
        "public" -> PrivacyLevel.Public
        "friend" -> PrivacyLevel.Friend
        else -> PrivacyLevel.Private
    }
    Surface(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(LifeMashSpacing.md),
        shadowElevation = LifeMashShadow.sm,
        color = MaterialTheme.colorScheme.surface,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = LifeMashSpacing.md, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(30.dp)
                    .clip(RoundedCornerShape(LifeMashSpacing.micro))
                    .background(barColor),
            )
            Spacer(Modifier.width(LifeMashSpacing.md))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                )
                if (event.startTime.isNotBlank()) {
                    Text(
                        text = "${event.startTime} – ${event.endTime}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = LifeMashSpacing.micro),
                    )
                }
            }
            Spacer(Modifier.width(LifeMashSpacing.sm))
            LifeMashPrivacyLabel(level = privacyLevel)
            Spacer(Modifier.width(LifeMashSpacing.sm))
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(RoundedCornerShape(LifeMashSpacing.sm))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { onCameraClick(event.id) },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "사진 업로드",
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

internal fun visibilityLabel(visibility: String): String {
    return when (visibility.lowercase()) {
        "public" -> "공개"
        "friend", "friends", "followers" -> "친구"
        else -> "비공개"
    }
}

@Composable
internal fun parseColor(hex: String, fallback: Color): Color {
    return runCatching {
        val clean = hex.trimStart('#')
        val r = clean.substring(0, 2).toInt(16)
        val g = clean.substring(2, 4).toInt(16)
        val b = clean.substring(4, 6).toInt(16)
        Color(
            r,
            g,
            b,
        )
    }.getOrElse { fallback }
}

internal fun daysInMonth(year: Int, month: Int): Int {
    return when (month) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        2 -> if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) 29 else 28
        else -> 30
    }
}

internal fun firstDayOfWeek(year: Int, month: Int): Int {
    // Tomohiko Sakamoto's algorithm (0=Sunday)
    val y = if (month < 3) year - 1 else year
    val m = if (month < 3) month + 10 else month - 2
    val c = y / 100
    val yy = y % 100
    return ((yy + yy / 4 + c / 4 - 2 * c + 26 * (m + 1) / 10 + 1 - 1) % 7 + 7) % 7
}
