package org.bmsk.lifemash.calendar.impl

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.People
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.time.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import org.bmsk.lifemash.domain.calendar.Event
import org.bmsk.lifemash.domain.calendar.EventVisibility
import org.bmsk.lifemash.designsystem.component.LifeMashChip
import org.bmsk.lifemash.designsystem.component.LifeMashInput
import org.bmsk.lifemash.designsystem.theme.LifeMashSpacing
import kotlin.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EventCreateScreen(
    uiState: EventCreateUiState,
    year: Int,
    month: Int,
    day: Int,
    existingEvent: Event? = null,
    onSave: (title: String, color: String?, dateTime: EventDateTime, location: String?, visibility: EventVisibility, memo: String?) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isEdit = existingEvent != null

    var title by remember { mutableStateOf(existingEvent?.title ?: "") }
    var location by remember { mutableStateOf(existingEvent?.location ?: "") }
    var showLocationInput by remember { mutableStateOf(false) }
    var selectedColor by remember { mutableStateOf<String?>(existingEvent?.color) }
    var visibility by remember { mutableStateOf(existingEvent?.visibility ?: EventVisibility.Followers) }
    var showVisibilitySheet by remember { mutableStateOf(false) }
    var memo by remember { mutableStateOf(existingEvent?.description ?: "") }
    var eventDateTime by remember {
        mutableStateOf(
            if (isEdit) {
                val tz = TimeZone.currentSystemDefault()
                val startLocal = existingEvent.startAt.toLocalDateTime(tz)
                val endLocal = existingEvent.endAt?.toLocalDateTime(tz)
                EventDateTime(
                    date = startLocal.date,
                    startTime = if (!existingEvent.isAllDay) TimeOfDay(startLocal.hour, startLocal.minute) else null,
                    endTime = if (!existingEvent.isAllDay && endLocal != null) TimeOfDay(endLocal.hour, endLocal.minute) else null,
                )
            } else if (day > 0) {
                EventDateTime(date = LocalDate(year, month, day))
            } else {
                EventDateTime(date = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date)
            }
        )
    }
    var pickerStep by remember { mutableStateOf<DateTimePickerStep>(DateTimePickerStep.Hidden) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(MaterialTheme.colorScheme.background),
    ) {
        // 상단바
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = LifeMashSpacing.xl, vertical = LifeMashSpacing.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(onClick = onCancel) {
                Text(
                    text = "취소",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = if (isEdit) "일정 수정" else "새 일정",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
            )
            if (uiState.isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(LifeMashSpacing.xl),
                    strokeWidth = LifeMashSpacing.micro,
                )
            } else {
                TextButton(
                    onClick = { onSave(title, selectedColor, eventDateTime, location.ifBlank { null }, visibility, memo.ifBlank { null }) },
                    enabled = title.isNotBlank(),
                ) {
                    Text(
                        text = "저장",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = if (title.isNotBlank()) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            // 제목 인라인 입력
            TextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = LifeMashSpacing.xl,
                        end = LifeMashSpacing.xl,
                        top = LifeMashSpacing.xl,
                        bottom = LifeMashSpacing.lg,
                    ),
                textStyle = TextStyle(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                ),
                placeholder = {
                    Text(
                        text = "어떤 일정인가요?",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.outlineVariant,
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.primary,
                ),
                singleLine = true,
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            // 날짜/시간 행
            EventFormRow(
                icon = Icons.Outlined.CalendarMonth,
                onClick = { pickerStep = DateTimePickerStep.PickDate },
            ) {
                Column {
                    Text(
                        text = eventDateTime.dateLabel(),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = eventDateTime.timeLabel(),
                        style = MaterialTheme.typography.bodySmall,
                        color = if (eventDateTime.startTime != null)
                            MaterialTheme.colorScheme.onSurface
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            // 위치 행
            EventFormRow(icon = Icons.Outlined.LocationOn, onClick = { showLocationInput = true }) {
                Text(
                    text = if (location.isBlank()) "위치 추가" else location,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (location.isBlank()) MaterialTheme.colorScheme.onSurfaceVariant
                    else MaterialTheme.colorScheme.onSurface,
                )
            }

            // 공개 범위 행
            EventFormRow(icon = Icons.Outlined.People, onClick = { showVisibilitySheet = true }) {
                Text(
                    text = "공개 범위",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.weight(1f))
                LifeMashChip(
                    text = visibility.label(),
                    selected = true,
                    onClick = { showVisibilitySheet = true },
                )
            }

            // 색상 행
            EventFormRow(icon = Icons.Outlined.Palette, onClick = null) {
                Text(
                    text = "색상",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.weight(1f))
                Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                    EVENT_COLORS.forEach { (hex, color) ->
                        val isSelected = selectedColor == hex
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(color)
                                .then(
                                    if (isSelected) Modifier.border(
                                        width = 2.5.dp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        shape = CircleShape,
                                    ) else Modifier
                                )
                                .clickable { selectedColor = if (isSelected) null else hex },
                        )
                    }
                }
            }

            // 메모 행
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = LifeMashSpacing.xl, vertical = 14.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Description,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(top = LifeMashSpacing.micro)
                        .size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                TextField(
                    value = memo,
                    onValueChange = { memo = it },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                    ),
                    placeholder = {
                        Text(
                            text = "메모 추가",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = MaterialTheme.colorScheme.primary,
                    ),
                    singleLine = false,
                    minLines = 1,
                )
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        }
    }

    // 날짜/시간 선택 다이얼로그
    when (pickerStep) {
        DateTimePickerStep.Hidden -> {}

        DateTimePickerStep.PickDate -> {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = eventDateTime.date
                    .atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds(),
            )
            DatePickerDialog(
                onDismissRequest = { pickerStep = DateTimePickerStep.Hidden },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val date = Instant.fromEpochMilliseconds(millis)
                                .toLocalDateTime(TimeZone.UTC).date
                            eventDateTime = eventDateTime.copy(date = date)
                        }
                        pickerStep = DateTimePickerStep.PickStartTime
                    }) { Text("확인") }
                },
                dismissButton = {
                    TextButton(onClick = { pickerStep = DateTimePickerStep.Hidden }) { Text("취소") }
                },
            ) {
                DatePicker(state = datePickerState)
            }
        }

        DateTimePickerStep.PickStartTime -> {
            TimePickerAlertDialog(
                title = "시작 시간",
                initialTime = eventDateTime.startTime ?: TimeOfDay(9, 0),
                onConfirm = { time ->
                    eventDateTime = eventDateTime.copy(startTime = time)
                    pickerStep = DateTimePickerStep.PickEndTime
                },
                onDismiss = { pickerStep = DateTimePickerStep.Hidden },
            )
        }

        DateTimePickerStep.PickEndTime -> {
            val defaultEnd = eventDateTime.startTime?.let {
                TimeOfDay((it.hour + 2).coerceAtMost(23), it.minute)
            } ?: TimeOfDay(11, 0)
            TimePickerAlertDialog(
                title = "종료 시간",
                initialTime = eventDateTime.endTime ?: defaultEnd,
                onConfirm = { time ->
                    eventDateTime = eventDateTime.copy(endTime = time)
                    pickerStep = DateTimePickerStep.Hidden
                },
                onDismiss = { pickerStep = DateTimePickerStep.Hidden },
            )
        }
    }

    // 공개 범위 바텀시트
    if (showVisibilitySheet) {
        VisibilitySheet(
            currentVisibility = visibility,
            onSelect = { visibility = it },
            onDismiss = { showVisibilitySheet = false },
        )
    }

    // 위치 입력 다이얼로그
    if (showLocationInput) {
        AlertDialog(
            onDismissRequest = { showLocationInput = false },
            title = { Text("위치") },
            text = {
                LifeMashInput(
                    value = location,
                    onValueChange = { location = it },
                    placeholder = "장소를 입력하세요",
                    singleLine = true,
                )
            },
            confirmButton = {
                TextButton(onClick = { showLocationInput = false }) { Text("확인") }
            },
            dismissButton = {
                TextButton(onClick = {
                    location = ""
                    showLocationInput = false
                }) { Text("삭제") }
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerAlertDialog(
    title: String,
    initialTime: TimeOfDay,
    onConfirm: (TimeOfDay) -> Unit,
    onDismiss: () -> Unit,
) {
    val state: TimePickerState = rememberTimePickerState(
        initialHour = initialTime.hour,
        initialMinute = initialTime.minute,
    )
    AlertDialog(
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

private fun EventVisibility.label(): String = when (this) {
    EventVisibility.Public -> "전체공개"
    EventVisibility.Followers -> "팔로워"
    is EventVisibility.Group -> "그룹"
    is EventVisibility.Specific -> "특정인"
    EventVisibility.Private -> "비공개"
}

@Composable
private fun EventFormRow(
    icon: ImageVector,
    onClick: (() -> Unit)? = {},
    showChevron: Boolean = true,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = LifeMashSpacing.xl, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        content()
        if (onClick != null && showChevron) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                modifier = Modifier.size(LifeMashSpacing.lg),
                tint = MaterialTheme.colorScheme.outlineVariant,
            )
        }
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
}