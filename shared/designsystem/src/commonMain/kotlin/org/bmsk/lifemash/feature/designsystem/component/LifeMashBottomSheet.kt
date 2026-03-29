package org.bmsk.lifemash.feature.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import org.bmsk.lifemash.feature.designsystem.theme.LifeMashRadius
import org.bmsk.lifemash.feature.designsystem.theme.LifeMashSpacing

data class BottomSheetOption(
    val icon: ImageVector,
    val label: String,
    val onClick: () -> Unit,
)

@Composable
fun LifeMashBottomSheetContent(
    title: String,
    options: List<BottomSheetOption>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = LifeMashRadius.xl, topEnd = LifeMashRadius.xl))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = LifeMashSpacing.xl, vertical = LifeMashSpacing.md),
    ) {
        // Handle
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(width = 36.dp, height = 4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(MaterialTheme.colorScheme.outlineVariant),
        )
        Spacer(modifier = Modifier.height(LifeMashSpacing.lg))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = LifeMashSpacing.lg),
        )
        options.forEachIndexed { index, option ->
            if (index > 0) {
                Divider(color = MaterialTheme.colorScheme.outlineVariant)
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = option.onClick)
                    .padding(vertical = LifeMashSpacing.md),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = option.icon,
                    contentDescription = option.label,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(modifier = Modifier.width(LifeMashSpacing.md))
                Text(
                    text = option.label,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
        Spacer(modifier = Modifier.height(LifeMashSpacing.xxl))
    }
}
