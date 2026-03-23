package org.bmsk.lifemash.calendar.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.Switch
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
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import org.bmsk.lifemash.calendar.domain.model.Event
import org.bmsk.lifemash.calendar.domain.model.Group
import org.bmsk.lifemash.calendar.domain.model.GroupType

@Composable
internal fun CalendarScreen(
    uiState: CalendarUiState,
    onDateSelect: (LocalDate) -> Unit,
    onChangeMonth: (year: Int, month: Int) -> Unit,
    onSelectGroup: (String) -> Unit,
    onShowOverlay: (CalendarOverlay) -> Unit,
    onCreateGroup: (GroupType, String?) -> Unit,
    onJoinGroup: (String) -> Unit,
) {
    when (uiState) {
        is CalendarUiState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is CalendarUiState.Error -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = uiState.message,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }

        is CalendarUiState.Loaded -> CalendarLoadedContent(
            uiState = uiState,
            onDateSelect = onDateSelect,
            onChangeMonth = onChangeMonth,
            onSelectGroup = onSelectGroup,
            onShowOverlay = onShowOverlay,
            onCreateGroup = onCreateGroup,
            onJoinGroup = onJoinGroup,
        )
    }
}

@Composable
private fun CalendarLoadedContent(
    uiState: CalendarUiState.Loaded,
    onDateSelect: (LocalDate) -> Unit,
    onChangeMonth: (year: Int, month: Int) -> Unit,
    onSelectGroup: (String) -> Unit,
    onShowOverlay: (CalendarOverlay) -> Unit,
    onCreateGroup: (GroupType, String?) -> Unit,
    onJoinGroup: (String) -> Unit,
) {
    when {
        uiState.groups.isEmpty() -> NoGroupContent(
            isCreating = uiState.isCreatingGroup,
            onCreateGroup = onCreateGroup,
            onJoinGroup = onJoinGroup,
        )

        else -> CalendarContent(
            uiState = uiState,
            onDateSelect = onDateSelect,
            onChangeMonth = onChangeMonth,
            onSelectGroup = onSelectGroup,
            onShowOverlay = onShowOverlay,
        )
    }
}

@Composable
private fun CalendarContent(
    uiState: CalendarUiState.Loaded,
    onDateSelect: (LocalDate) -> Unit,
    onChangeMonth: (year: Int, month: Int) -> Unit,
    onSelectGroup: (String) -> Unit,
    onShowOverlay: (CalendarOverlay) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
        Column(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
            if (uiState.groups.size > 1) {
                GroupSelector(
                    groups = uiState.groups,
                    selectedGroup = uiState.selectedGroup,
                    onSelectGroup = onSelectGroup,
                    onRenameGroup = { onShowOverlay(CalendarOverlay.GroupRename) },
                )
                Spacer(Modifier.height(8.dp))
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
                    onChangeMonth(y, m)
                },
                onNext = {
                    val (y, m) = if (uiState.currentMonth == 12) {
                        uiState.currentYear + 1 to 1
                    } else {
                        uiState.currentYear to uiState.currentMonth + 1
                    }
                    onChangeMonth(y, m)
                },
            )

            MonthGrid(
                year = uiState.currentYear,
                month = uiState.currentMonth,
                selectedDate = uiState.selectedDate,
                events = uiState.events,
                onDateSelect = onDateSelect,
            )

            HorizontalDivider(Modifier.padding(vertical = 8.dp))

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
                    modifier = Modifier.padding(top = 16.dp),
                )

                else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
            onClick = { onShowOverlay(CalendarOverlay.EventCreate(uiState.selectedDate)) },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
        ) {
            Icon(Icons.Filled.Add, contentDescription = "일정 추가")
        }
    }
}

// region BottomSheet / Dialog (internal — RouteScreen에서 사용)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
internal fun EventCreateBottomSheet(
    editingEvent: Event?,
    isLoading: Boolean,
    selectedDate: LocalDate?,
    onDismiss: () -> Unit,
    onSubmit: (EventFormData) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val isEdit = editingEvent != null

    var title by remember { mutableStateOf(editingEvent?.title.orEmpty()) }
    var description by remember { mutableStateOf(editingEvent?.description.orEmpty()) }
    var isAllDay by remember { mutableStateOf(editingEvent?.isAllDay ?: false) }
    var selectedColor by remember { mutableStateOf(editingEvent?.color) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = if (isEdit) "일정 수정" else "새 일정",
                style = MaterialTheme.typography.titleLarge,
            )

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("제목") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("설명 (선택)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("종일", style = MaterialTheme.typography.bodyLarge)
                Switch(checked = isAllDay, onCheckedChange = { isAllDay = it })
            }

            Text("색상", style = MaterialTheme.typography.bodyLarge)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                EVENT_COLORS.forEach { (hex, color) ->
                    val isSelectedColor = selectedColor == hex
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(color)
                            .then(
                                if (isSelectedColor) {
                                    Modifier.padding(2.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                } else {
                                    Modifier
                                }
                            )
                            .clickable { selectedColor = if (isSelectedColor) null else hex },
                        contentAlignment = Alignment.Center,
                    ) {
                        if (isSelectedColor) {
                            Box(
                                Modifier.size(16.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surface)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    val startMs = if (isEdit) {
                        editingEvent!!.startAt.toEpochMilliseconds()
                    } else {
                        val date = selectedDate ?: return@Button
                        LocalDateTime(date.year, date.monthNumber, date.dayOfMonth, 0, 0)
                            .toInstant(TimeZone.currentSystemDefault())
                            .toEpochMilliseconds()
                    }
                    onSubmit(
                        EventFormData(
                            title = title.ifBlank { editingEvent?.title ?: "" },
                            description = description.ifBlank { null },
                            startAt = startMs,
                            endAt = null,
                            isAllDay = isAllDay,
                            color = selectedColor,
                        ),
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotBlank() && !isLoading,
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    Spacer(Modifier.width(8.dp))
                }
                Text(if (isEdit) "수정" else "저장")
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EventDetailBottomSheet(
    event: Event,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showDeleteConfirm by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    val eventColor = event.color
                    if (eventColor != null) {
                        Box(
                            Modifier.size(12.dp)
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
                    IconButton(onClick = onEdit) {
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

            Spacer(Modifier.height(24.dp))
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
) {
    var inviteCode by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "공유 캘린더를 시작하세요",
            style = MaterialTheme.typography.headlineSmall,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "그룹을 만들거나 초대 코드로 참여하세요",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(32.dp))

        Button(
            onClick = { onCreateGroup(GroupType.COUPLE, null) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isCreating,
        ) {
            if (isCreating) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                Spacer(Modifier.width(8.dp))
            }
            Text("그룹 만들기")
        }

        Spacer(Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = inviteCode,
            onValueChange = { inviteCode = it },
            label = { Text("초대 코드") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isCreating,
        )
        Spacer(Modifier.height(12.dp))
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
        horizontalArrangement = Arrangement.spacedBy(8.dp),
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
                            modifier = Modifier.size(16.dp).clickable { onRenameGroup(group.id) },
                        )
                    }
                } else null,
            )
        }
    }
}

@Composable
private fun MonthHeader(year: Int, month: Int, onPrev: () -> Unit, onNext: () -> Unit) {
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
    val daysInMonth = LocalDate(year, month, 1).let {
        when (month) {
            2 -> if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) 29 else 28
            4, 6, 9, 11 -> 30
            else -> 31
        }
    }
    val firstDayOfWeek = LocalDate(year, month, 1).dayOfWeek.ordinal

    val days = (1..daysInMonth).map { day -> LocalDate(year, month, day) }
    val paddedDays: List<LocalDate?> = List(firstDayOfWeek) { null } + days

    Row(Modifier.fillMaxWidth()) {
        listOf("월", "화", "수", "목", "금", "토", "일").forEach {
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
                            text = "${date.dayOfMonth}",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            },
                        )
                        if (hasEvent) {
                            Box(
                                Modifier.size(4.dp)
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
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        val dotColor = event.color?.let { parseEventColor(it) } ?: MaterialTheme.colorScheme.primary
        Box(
            Modifier.size(8.dp)
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
    "#FF6B6B" to Color(0xFFFF6B6B),
    "#4ECDC4" to Color(0xFF4ECDC4),
    "#45B7D1" to Color(0xFF45B7D1),
    "#96CEB4" to Color(0xFF96CEB4),
    "#FFEAA7" to Color(0xFFFFEAA7),
    "#DDA0DD" to Color(0xFFDDA0DD),
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
