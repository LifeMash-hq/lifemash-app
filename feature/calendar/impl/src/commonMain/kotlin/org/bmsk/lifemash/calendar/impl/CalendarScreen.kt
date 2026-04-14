package org.bmsk.lifemash.calendar.impl

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.PersistentList
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number
import org.bmsk.lifemash.domain.calendar.Event
import org.bmsk.lifemash.domain.calendar.Group
import org.bmsk.lifemash.domain.calendar.GroupType
import org.bmsk.lifemash.designsystem.component.LifeMashButton
import org.bmsk.lifemash.designsystem.component.LifeMashInput
import org.bmsk.lifemash.designsystem.theme.LifeMashSpacing

@Composable
internal fun CalendarScreen(
    uiState: CalendarUiState,
    onDateSelect: (LocalDate) -> Unit,
    onChangeMonth: (groupId: String, year: Int, month: Int) -> Unit,
    onSelectGroup: (String) -> Unit,
    onShowOverlay: (CalendarOverlay) -> Unit,
    onCreateGroup: (GroupType, String?) -> Unit,
    onJoinGroup: (String) -> Unit,
    onNavigateToEventCreate: (year: Int, month: Int, day: Int, groupId: String?) -> Unit = { _, _, _, _ -> },
    onNavigateToEventEdit: (groupId: String, event: Event) -> Unit = { _, _ -> },
    onBack: () -> Unit = {},
) {
    when (uiState.screenType) {
        CalendarUiState.ScreenType.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        CalendarUiState.ScreenType.NoGroup -> NoGroupContent(
            isCreating = uiState.isCreatingGroup,
            onCreateGroup = onCreateGroup,
            onJoinGroup = onJoinGroup,
            onBack = onBack,
        )

        CalendarUiState.ScreenType.Calendar -> CalendarContent(
            uiState = uiState,
            onDateSelect = onDateSelect,
            onChangeMonth = onChangeMonth,
            onSelectGroup = onSelectGroup,
            onShowOverlay = onShowOverlay,
            onNavigateToEventCreate = onNavigateToEventCreate,
            onNavigateToEventEdit = onNavigateToEventEdit,
            onBack = onBack,
        )
    }
}

@Composable
private fun CalendarContent(
    uiState: CalendarUiState,
    onDateSelect: (LocalDate) -> Unit,
    onChangeMonth: (groupId: String, year: Int, month: Int) -> Unit,
    onSelectGroup: (String) -> Unit,
    onShowOverlay: (CalendarOverlay) -> Unit,
    onNavigateToEventCreate: (year: Int, month: Int, day: Int, groupId: String?) -> Unit,
    onNavigateToEventEdit: (groupId: String, event: Event) -> Unit,
    onBack: () -> Unit = {},
) {
    val groupId = uiState.selectedGroup?.id

    Box(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
        Column(Modifier.fillMaxSize().padding(horizontal = LifeMashSpacing.lg)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로가기")
                }
                Text(
                    text = "그룹 캘린더",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f),
                )
            }
            if (uiState.groups.size > 1) {
                GroupSelector(
                    groups = uiState.groups,
                    selectedGroup = uiState.selectedGroup,
                    onSelectGroup = onSelectGroup,
                    onRenameGroup = { onShowOverlay(CalendarOverlay.GroupRename) },
                )
                Spacer(Modifier.height(LifeMashSpacing.sm))
            }

            MonthHeader(
                year = uiState.currentYear,
                month = uiState.currentMonth,
                onPrev = {
                    val (y, m) = if (uiState.currentMonth == 1) {
                        uiState.currentYear - 1 to 12
                    } else {
                        uiState.currentYear to uiState.currentMonth - 1
                    }
                    groupId?.let { onChangeMonth(it, y, m) }
                },
                onNext = {
                    val (y, m) = if (uiState.currentMonth == 12) {
                        uiState.currentYear + 1 to 1
                    } else {
                        uiState.currentYear to uiState.currentMonth + 1
                    }
                    groupId?.let { onChangeMonth(it, y, m) }
                },
            )

            MonthGrid(
                year = uiState.currentYear,
                month = uiState.currentMonth,
                selectedDate = uiState.selectedDate,
                events = uiState.events,
                onDateSelect = onDateSelect,
            )

            HorizontalDivider(Modifier.padding(vertical = LifeMashSpacing.sm))

            val selectedEvents = uiState.events
                .filter { event ->
                    uiState.selectedDate?.let { date ->
                        event.startAt.toString().startsWith(date.toString())
                    } == true
                }

            when {
                selectedEvents.isEmpty() -> Text(
                    text = "선택한 날짜에 일정이 없습니다",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = LifeMashSpacing.lg),
                )

                else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(LifeMashSpacing.sm)) {
                    items(selectedEvents) { event ->
                        EventItem(
                            event = event,
                            onClick = { onShowOverlay(CalendarOverlay.EventDetail(event)) },
                        )
                    }
                }
            }
        }
        FloatingActionButton(
            onClick = {
                val date = uiState.selectedDate
                onNavigateToEventCreate(
                    date?.year ?: uiState.currentYear,
                    date?.let { it.month.number } ?: uiState.currentMonth,
                    date?.day ?: 0,
                    uiState.selectedGroup?.id,
                )
            },
            modifier = Modifier.align(Alignment.BottomEnd).padding(LifeMashSpacing.lg),
        ) {
            Icon(Icons.Filled.Add, contentDescription = "일정 추가")
        }
    }
}

// region BottomSheet / Dialog (internal — RouteScreen에서 사용)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EventDetailBottomSheet(
    event: Event,
    onDismiss: () -> Unit,
    onEdit: (Event) -> Unit,
    onDelete: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showDeleteConfirm by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = LifeMashSpacing.xxl, vertical = LifeMashSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(LifeMashSpacing.md),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(LifeMashSpacing.sm),
                ) {
                    val eventColor = event.color
                    if (eventColor != null) {
                        Box(
                            Modifier.size(LifeMashSpacing.md)
                                .clip(CircleShape)
                                .background(parseEventColor(eventColor))
                        )
                    }
                    Text(
                        text = event.title,
                        style = MaterialTheme.typography.titleLarge,
                    )
                }
                Row {
                    IconButton(onClick = { onEdit(event) }) {
                        Icon(Icons.Filled.Edit, contentDescription = "수정")
                    }
                    IconButton(onClick = { showDeleteConfirm = true }) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = "삭제",
                            tint = MaterialTheme.colorScheme.error,
                        )
                    }
                }
            }

            val detailDesc = event.description
            if (!detailDesc.isNullOrBlank()) {
                Text(
                    text = detailDesc,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            if (event.isAllDay) {
                Text(
                    text = "종일",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(Modifier.height(LifeMashSpacing.xxl))
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("일정 삭제") },
            text = { Text("\"${event.title}\" 일정을 삭제하시겠습니까?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirm = false
                        onDelete()
                    },
                ) {
                    Text("삭제", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("취소")
                }
            },
        )
    }
}

@Composable
internal fun GroupRenameDialog(
    currentName: String,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    var name by remember { mutableStateOf(currentName) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("그룹명 변경") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("그룹명") },
                singleLine = true,
                enabled = !isLoading,
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(name) },
                enabled = name.isNotBlank() && !isLoading,
            ) { Text("변경") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("취소") }
        },
    )
}

// endregion

// region Private composables

@Composable
private fun NoGroupContent(
    isCreating: Boolean,
    onCreateGroup: (GroupType, String?) -> Unit,
    onJoinGroup: (String) -> Unit,
    onBack: () -> Unit = {},
) {
    var inviteCode by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().statusBarsPadding().padding(LifeMashSpacing.xxxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로가기")
            }
        }
        Text(
            text = "공유 캘린더를 시작하세요",
            style = MaterialTheme.typography.headlineSmall,
        )
        Spacer(Modifier.height(LifeMashSpacing.sm))
        Text(
            text = "그룹을 만들거나 초대 코드로 참여하세요",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(LifeMashSpacing.xxxl))

        Button(
            onClick = { onCreateGroup(GroupType.COUPLE, null) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isCreating,
        ) {
            if (isCreating) {
                CircularProgressIndicator(modifier = Modifier.size(LifeMashSpacing.xl), strokeWidth = LifeMashSpacing.micro)
                Spacer(Modifier.width(LifeMashSpacing.sm))
            }
            Text("그룹 만들기")
        }

        Spacer(Modifier.height(LifeMashSpacing.xxl))
        HorizontalDivider()
        Spacer(Modifier.height(LifeMashSpacing.xxl))

        OutlinedTextField(
            value = inviteCode,
            onValueChange = { inviteCode = it },
            label = { Text("초대 코드") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isCreating,
        )
        Spacer(Modifier.height(LifeMashSpacing.md))
        OutlinedButton(
            onClick = { onJoinGroup(inviteCode) },
            modifier = Modifier.fillMaxWidth(),
            enabled = inviteCode.isNotBlank() && !isCreating,
        ) {
            Text("초대 코드로 참여")
        }
    }
}

@Composable
private fun GroupSelector(
    groups: PersistentList<Group>,
    selectedGroup: Group?,
    onSelectGroup: (String) -> Unit,
    onRenameGroup: (String) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(LifeMashSpacing.sm),
    ) {
        groups.forEach { group ->
            FilterChip(
                selected = group.id == selectedGroup?.id,
                onClick = { onSelectGroup(group.id) },
                label = { Text(group.name ?: group.type.name) },
                trailingIcon = if (group.id == selectedGroup?.id) {
                    {
                        Icon(
                            Icons.Filled.Edit,
                            contentDescription = "그룹명 변경",
                            modifier = Modifier.size(LifeMashSpacing.lg).clickable { onRenameGroup(group.id) },
                        )
                    }
                } else null,
            )
        }
    }
}

@Composable
private fun MonthHeader(
    year: Int,
    month: Int,
    onPrev: () -> Unit, onNext: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onPrev) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "이전 달")
        }
        Text(
            text = "${year}년 ${month}월",
            style = MaterialTheme.typography.titleLarge,
        )
        IconButton(onClick = onNext) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "다음 달")
        }
    }
}

@Composable
private fun MonthGrid(
    year: Int,
    month: Int,
    selectedDate: LocalDate?,
    events: PersistentList<Event>,
    onDateSelect: (LocalDate) -> Unit,
) {
    val daysInMonth = LocalDate(
        year,
        month,
        1,
    ).let {
        when (month) {
            2 -> if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) 29 else 28
            4, 6, 9, 11 -> 30
            else -> 31
        }
    }
    // kotlinx-datetime ordinal: Monday=0..Sunday=6 → Sunday-first: (ordinal+1)%7
    val firstDayOfWeek = (LocalDate(year, month, 1).dayOfWeek.ordinal + 1) % 7

    val days = (1..daysInMonth).map { day -> LocalDate(
        year,
        month,
        day,
    ) }
    val paddedDays: List<LocalDate?> = List(firstDayOfWeek) { null } + days

    Row(Modifier.fillMaxWidth()) {
        listOf(
            "일",
            "월",
            "화",
            "수",
            "목",
            "금",
            "토",
        ).forEach {
            Text(
                text = it,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier.fillMaxWidth(),
    ) {
        items(paddedDays) { date ->
            if (date == null) {
                Box(Modifier.aspectRatio(1f))
            } else {
                val isSelected = date == selectedDate
                val hasEvent = events.any { it.startAt.toString().startsWith(date.toString()) }

                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(CircleShape)
                        .then(
                            if (isSelected) {
                                Modifier.background(MaterialTheme.colorScheme.primary)
                            } else {
                                Modifier
                            }
                        )
                        .clickable { onDateSelect(date) },
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${date.day}",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            },
                        )
                        if (hasEvent) {
                            Box(
                                Modifier.size(LifeMashSpacing.xxs)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) {
                                            MaterialTheme.colorScheme.onPrimary
                                        } else {
                                            MaterialTheme.colorScheme.primary
                                        }
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EventItem(event: Event, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(LifeMashSpacing.sm))
            .clickable(onClick = onClick)
            .padding(vertical = LifeMashSpacing.sm, horizontal = LifeMashSpacing.xxs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(LifeMashSpacing.sm),
    ) {
        val dotColor = event.color?.let { parseEventColor(it) } ?: MaterialTheme.colorScheme.primary
        Box(
            Modifier.size(LifeMashSpacing.sm)
                .clip(CircleShape)
                .background(dotColor)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(text = event.title, style = MaterialTheme.typography.bodyMedium)
            val desc = event.description
            if (!desc.isNullOrBlank()) {
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                )
            }
        }
    }
}

// endregion

// region Color utils

internal val EVENT_COLORS = listOf(
    "#6C5CE7" to Color(0xFF6C5CE7),
    "#E17055" to Color(0xFFE17055),
    "#00B894" to Color(0xFF00B894),
    "#FF6B81" to Color(0xFFFF6B81),
    "#0984E3" to Color(0xFF0984E3),
    "#FDCB6E" to Color(0xFFFDCB6E),
)

internal fun parseEventColor(hex: String): Color {
    return EVENT_COLORS.find { it.first.equals(hex, ignoreCase = true) }?.second
        ?: try {
            Color(hex.removePrefix("#").toLong(16) or 0xFF000000)
        } catch (_: Exception) {
            Color.Unspecified
        }
}

// endregion