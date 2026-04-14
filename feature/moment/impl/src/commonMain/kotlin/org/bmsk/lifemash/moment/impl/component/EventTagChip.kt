package org.bmsk.lifemash.moment.impl.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.bmsk.lifemash.designsystem.component.LifeMashChip
import org.bmsk.lifemash.designsystem.theme.LifeMashSpacing

@Composable
internal fun EventTagChip(
    eventTitle: String?,
    onChangeClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.padding(bottom = LifeMashSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LifeMashChip(
            text = if (eventTitle != null) "📅 $eventTitle" else "📅 일정 추가",
            selected = eventTitle != null,
            onClick = onChangeClick,
        )
        if (eventTitle != null) {
            Spacer(Modifier.width(LifeMashSpacing.sm))
            TextButton(onClick = onChangeClick) {
                Text(
                    text = "일정 변경",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}
