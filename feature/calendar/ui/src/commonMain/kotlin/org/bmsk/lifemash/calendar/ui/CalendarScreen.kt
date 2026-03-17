package org.bmsk.lifemash.calendar.ui

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.PersistentList
import kotlinx.datetime.LocalDate
import org.bmsk.lifemash.calendar.domain.model.Event

@Composable
internal fun CalendarScreen(
    uiState: CalendarUiState,
    onDateSelect: (LocalDate) -> Unit,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
) {
    if (uiState.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (uiState.groups.isNullOrEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "그룹에 참여하면 캘린더를 사용할 수 있습니다",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        return
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        MonthHeader(
            year = uiState.currentYear,
            month = uiState.currentMonth,
            onPrev = onPrevMonth,
            onNext = onNextMonth,
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
            ?.filter { event ->
                uiState.selectedDate?.let { date ->
                    event.startAt.toString().startsWith(date.toString())
                } == true
            }

        if (selectedEvents.isNullOrEmpty()) {
            Text(
                text = "선택한 날짜에 일정이 없습니다",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 16.dp),
            )
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(selectedEvents) { event ->
                    EventItem(event)
                }
            }
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
    events: PersistentList<Event>?,
    onDateSelect: (LocalDate) -> Unit,
) {
    val daysInMonth = LocalDate(year, month, 1).let {
        when (month) {
            2 -> if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) 29 else 28
            4, 6, 9, 11 -> 30
            else -> 31
        }
    }
    val firstDayOfWeek = LocalDate(year, month, 1).dayOfWeek.ordinal // 0=Monday

    val days = (1..daysInMonth).map { day -> LocalDate(year, month, day) }
    val paddedDays: List<LocalDate?> = List(firstDayOfWeek) { null } + days

    // 요일 헤더
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
                val hasEvent = events?.any { it.startAt.toString().startsWith(date.toString()) } == true

                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(CircleShape)
                        .then(
                            if (isSelected) Modifier.background(MaterialTheme.colorScheme.primary)
                            else Modifier
                        )
                        .clickable { onDateSelect(date) },
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${date.dayOfMonth}",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onSurface,
                        )
                        if (hasEvent) {
                            Box(
                                Modifier.size(4.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.onPrimary
                                        else MaterialTheme.colorScheme.primary
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
private fun EventItem(event: Event) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            Modifier.size(8.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
        )
        Text(text = event.title, style = MaterialTheme.typography.bodyMedium)
    }
}
