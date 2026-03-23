package org.bmsk.lifemash.home.ui.blocks

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.bmsk.lifemash.home.api.BlocksTodayData

@Composable
internal fun CalendarTodayBlock(todayData: BlocksTodayData?) {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "오늘 일정", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            if (todayData == null || todayData.todayEvents.isEmpty()) {
                Text(
                    text = "일정 없음",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                todayData.todayEvents.forEach { event ->
                    Text(
                        text = if (event.allDay) event.title else "${event.startTime.take(5)} ${event.title}",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}
