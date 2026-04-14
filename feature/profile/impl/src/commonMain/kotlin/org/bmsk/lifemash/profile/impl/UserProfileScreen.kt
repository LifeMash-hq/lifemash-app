package org.bmsk.lifemash.profile.impl

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.bmsk.lifemash.designsystem.component.LifeMashButton
import org.bmsk.lifemash.designsystem.component.LifeMashButtonStyle
import org.bmsk.lifemash.designsystem.theme.LifeMashSpacing
import androidx.compose.material.icons.filled.Add

@Composable
internal fun UserProfileScreen(
    uiState: ProfileUiState,
    onBackClick: () -> Unit = {},
    onFollowToggle: () -> Unit = {},
    onFollowerClick: () -> Unit = {},
    onFollowingClick: () -> Unit = {},
    onMomentClick: (String) -> Unit = {},
    onSubTabSelect: (ProfileSubTab) -> Unit = {},
    onCalendarDaySelect: (Int?) -> Unit = {},
    onNavigateMonth: (Int) -> Unit = {},
    onNavigateToEventCreate: (year: Int, month: Int, day: Int) -> Unit = { _, _, _ -> },
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
                            // Back button row
                            Box(modifier = Modifier.fillMaxWidth().padding(horizontal = LifeMashSpacing.xxs)) {
                                IconButton(onClick = onBackClick) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "뒤로가기",
                                        tint = MaterialTheme.colorScheme.onSurface,
                                    )
                                }
                            }
                        }
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
                                        text = if (uiState.profile.isFollowing) "팔로잉" else "팔로우",
                                        onClick = onFollowToggle,
                                        modifier = Modifier.fillMaxWidth(),
                                        style = if (uiState.profile.isFollowing) LifeMashButtonStyle.Outline else LifeMashButtonStyle.Primary,
                                    )
                                },
                            )
                        }
                        when (uiState.selectedSubTab) {
                            ProfileSubTab.Moments -> {
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
                            ProfileSubTab.Calendar -> {
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
                                        onCameraClick = {},
                                        onEventClick = onEventClick,
                                    )
                                }
                                item { Spacer(Modifier.height(80.dp)) }
                            }
                        }
                    }

                    if (uiState.selectedSubTab == ProfileSubTab.Calendar) {
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
