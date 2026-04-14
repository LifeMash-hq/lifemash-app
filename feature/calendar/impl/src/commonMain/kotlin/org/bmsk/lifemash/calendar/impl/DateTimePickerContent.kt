package org.bmsk.lifemash.calendar.impl

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.number
import kotlinx.datetime.plus
import org.bmsk.lifemash.designsystem.component.LifeMashCenterTopBar
import org.bmsk.lifemash.designsystem.component.LifeMashToggle
import org.bmsk.lifemash.designsystem.theme.LifeMashSpacing

private val DAYS_OF_WEEK = listOf("월", "화", "수", "목", "금", "토", "일")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DateTimePickerContent(
    dateTime: EventDateTime,
    onDateTimeChange: (EventDateTime) -> Unit,
    onBack: () -> Unit,
) {
    var localDateTime by remember(dateTime) { mutableStateOf(dateTime) }
    var viewYear by remember { mutableStateOf(dateTime.date.year) }
    var viewMonth by remember { mutableStateOf(dateTime.date.monthNumber) }
    var showTimePicker by remember { mutableStateOf<TimePickerTarget?>(null) }

    Column(Modifier.fillMaxSize().statusBarsPadding()) {
        LifeMashCenterTopBar(
            title = "날짜 및 시간",
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                }
            },
            actions = {
                TextButton(onClick = {
                    onDateTimeChange(localDateTime)
                }) {
                    Text(
                        text = "완료",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            },
        )

        // 종일 토글
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = LifeMashSpacing.xl, vertical = LifeMashSpacing.md),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "종일",
                style = MaterialTheme.typography.bodyLarge,
            )
            LifeMashToggle(
                checked = localDateTime.isAllDay,
                onCheckedChange = { isAllDay ->
                    localDateTime = if (isAllDay) {
                        localDateTime.copy(startTime = null, endTime = null)
                    } else {
                        localDateTime.copy(
                            startTime = TimeOfDay(9, 0),
                            endTime = TimeOfDay(11, 0),
                        )
                    }
                },
            )
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        // 캘린더 헤더
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = LifeMashSpacing.xl, vertical = LifeMashSpacing.md),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = {
                if (viewMonth == 1) { viewMonth = 12; viewYear-- }
                else viewMonth--
            }) {
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "이전 달",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = "${viewYear}년 ${viewMonth}월",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            )
            IconButton(onClick = {
                if (viewMonth == 12) { viewMonth = 1; viewYear++ }
                else viewMonth++
            }) {
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "다음 달",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        // 요일 헤더
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = LifeMashSpacing.xl),
        ) {
            DAYS_OF_WEEK.forEach { dow ->
                Text(
                    text = dow,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        }

        // 캘린더 그리드
        val cells = buildCalendarCells(viewYear, viewMonth)
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = LifeMashSpacing.xl, vertical = LifeMashSpacing.xs),
            userScrollEnabled = false,
        ) {
            items(cells.size) { index ->
                val day = cells[index]
                CalendarDayCell(
                    day = day,
                    isSelected = day != null && localDateTime.date == LocalDate(viewYear, viewMonth, day),
                    onClick = {
                        if (day != null) {
                            localDateTime = localDateTime.copy(date = LocalDate(viewYear, viewMonth, day))
                        }
                    },
                )
            }
        }

        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant,
            modifier = Modifier.padding(horizontal = LifeMashSpacing.xl),
        )

        // 시간 선택 (종일 아닐 때만)
        if (!localDateTime.isAllDay) {
            TimeRow(
                label = "시작 시간",
                value = localDateTime.startTime?.formatted() ?: "09:00",
                onClick = { showTimePicker = TimePickerTarget.START },
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            TimeRow(
                label = "종료 시간",
                value = localDateTime.endTime?.formatted() ?: "11:00",
                onClick = { showTimePicker = TimePickerTarget.END },
            )
        }
    }

    // 시간 선택 다이얼로그
    showTimePicker?.let { target ->
        val initial = when (target) {
            TimePickerTarget.START -> localDateTime.startTime ?: TimeOfDay(9, 0)
            TimePickerTarget.END -> localDateTime.endTime ?: TimeOfDay(11, 0)
        }
        TimePickerDialog(
            title = if (target == TimePickerTarget.START) "시작 시간" else "종료 시간",
            initialTime = initial,
            onConfirm = { time ->
                localDateTime = when (target) {
                    TimePickerTarget.START -> localDateTime.copy(startTime = time)
                    TimePickerTarget.END -> localDateTime.copy(endTime = time)
                }
                showTimePicker = null
            },
            onDismiss = { showTimePicker = null },
        )
    }
}

private enum class TimePickerTarget { START, END }

@Composable
private fun CalendarDayCell(
    day: Int?,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(CircleShape)
            .then(
                if (isSelected) Modifier.background(MaterialTheme.colorScheme.primary)
                else Modifier
            )
            .then(
                if (day != null) Modifier.clickable(onClick = onClick)
                else Modifier
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (day != null) {
            Text(
                text = day.toString(),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                ),
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun TimeRow(
    label: String,
    value: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = LifeMashSpacing.xl, vertical = LifeMashSpacing.md),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
        )
        Box(
            modifier = Modifier
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = LifeMashSpacing.md, vertical = LifeMashSpacing.xs),
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(
    title: String,
    initialTime: TimeOfDay,
    onConfirm: (TimeOfDay) -> Unit,
    onDismiss: () -> Unit,
) {
    val state = rememberTimePickerState(
        initialHour = initialTime.hour,
        initialMinute = initialTime.minute,
    )
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { TimePicker(state = state) },
        confirmButton = {
            TextButton(onClick = { onConfirm(TimeOfDay(state.hour, state.minute)) }) {
                Text("확인")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("취소") }
        },
    )
}

private fun buildCalendarCells(year: Int, month: Int): List<Int?> {
    val firstDay = LocalDate(year, month, 1)
    val startOffset = (firstDay.dayOfWeek.isoDayNumber - 1) // 월=0, 화=1, ..., 일=6
    val daysInMonth = firstDay.daysInMonth()
    val cells = mutableListOf<Int?>()
    repeat(startOffset) { cells.add(null) }
    for (day in 1..daysInMonth) { cells.add(day) }
    while (cells.size % 7 != 0) { cells.add(null) }
    return cells
}

private fun LocalDate.daysInMonth(): Int {
    val nextMonth = this.plus(1, DateTimeUnit.MONTH)
    val firstOfNext = LocalDate(nextMonth.year, nextMonth.monthNumber, 1)
    return firstOfNext.minus(1, DateTimeUnit.DAY).day
}
