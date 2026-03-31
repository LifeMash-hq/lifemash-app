package org.bmsk.lifemash.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.bmsk.lifemash.designsystem.theme.LifeMashRadius
import org.bmsk.lifemash.designsystem.theme.LifeMashShadow
import org.bmsk.lifemash.designsystem.theme.LifeMashSpacing
import org.bmsk.lifemash.designsystem.theme.LocalLifeMashColors

@Composable
fun LifeMashSegmentControl(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val semantic = LocalLifeMashColors.current

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(LifeMashRadius.md))
            .background(semantic.chipBg)
            .padding(LifeMashSpacing.micro),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        options.forEachIndexed { index, label ->
            if (index == selectedIndex) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    shadowElevation = LifeMashShadow.sm,
                    color = MaterialTheme.colorScheme.surface,
                ) {
                    Text(
                        text = label,
                        modifier = Modifier.padding(horizontal = LifeMashSpacing.md, vertical = LifeMashSpacing.xs),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            } else {
                Text(
                    text = label,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { onSelect(index) }
                        .padding(horizontal = LifeMashSpacing.md, vertical = LifeMashSpacing.xs),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
