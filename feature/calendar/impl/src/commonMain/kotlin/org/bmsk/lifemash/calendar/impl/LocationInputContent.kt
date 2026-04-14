package org.bmsk.lifemash.calendar.impl

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import org.bmsk.lifemash.designsystem.component.LifeMashInput
import org.bmsk.lifemash.designsystem.theme.LifeMashSpacing

@Composable
internal fun LocationInputContent(
    location: String,
    onLocationChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onBack: () -> Unit,
) {
    Column(Modifier.fillMaxSize().statusBarsPadding()) {
        LifeMashCenterTopBar(
            title = "위치",
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                }
            },
            actions = {
                TextButton(onClick = onConfirm) { Text("완료") }
            },
        )
        // TODO: implement location search
        Box(
            modifier = Modifier.fillMaxSize().padding(horizontal = LifeMashSpacing.xl),
            contentAlignment = Alignment.Center,
        ) {
            LifeMashInput(
                value = location,
                onValueChange = onLocationChange,
                placeholder = "장소를 입력하세요",
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
