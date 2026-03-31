package org.bmsk.lifemash.designsystem.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import org.bmsk.lifemash.designsystem.theme.LifeMashSpacing

@Composable
fun LifeMashProfileSubTabs(
    tabs: List<String>,
    selectedIndex: Int,
    onTabSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val primary = MaterialTheme.colorScheme.primary
    val outlineVariant = MaterialTheme.colorScheme.outlineVariant

    Box(modifier = modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth()) {
            tabs.forEachIndexed { index, label ->
                val isSelected = index == selectedIndex
                val textColor = if (isSelected) primary else MaterialTheme.colorScheme.onSurfaceVariant

                Text(
                    text = label,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onTabSelect(index) }
                        .drawBehind {
                            val borderWidth = 2.dp.toPx()
                            if (isSelected) {
                                drawLine(
                                    color = primary,
                                    start = Offset(0f, size.height),
                                    end = Offset(size.width, size.height),
                                    strokeWidth = borderWidth,
                                )
                            }
                        }
                        .padding(vertical = LifeMashSpacing.md),
                    style = MaterialTheme.typography.labelLarge,
                    color = textColor,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                )
            }
        }
        HorizontalDivider(
            modifier = Modifier.align(Alignment.BottomCenter),
            color = outlineVariant,
            thickness = 1.dp,
        )
    }
}
