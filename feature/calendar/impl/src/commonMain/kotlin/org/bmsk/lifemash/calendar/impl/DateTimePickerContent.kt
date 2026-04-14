package org.bmsk.lifemash.calendar.impl

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.bmsk.lifemash.designsystem.component.LifeMashCenterTopBar

@Composable
internal fun DateTimePickerContent(
    dateTime: EventDateTime,
    onDateTimeChange: (EventDateTime) -> Unit,
    onBack: () -> Unit,
) {
    Column(Modifier.fillMaxSize().statusBarsPadding()) {
        LifeMashCenterTopBar(
            title = "날짜 및 시간",
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                }
            },
            actions = {
                TextButton(onClick = onBack) { Text("완료") }
            },
        )
        // TODO: implement custom calendar + time picker
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("날짜 선택 (구현 예정)")
        }
    }
}
