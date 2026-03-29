package org.bmsk.lifemash.feature.designsystem.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.bmsk.lifemash.feature.designsystem.theme.LifeMashRadius

@Composable
fun LifeMashCard(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.surface,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = color,
        shape = RoundedCornerShape(LifeMashRadius.lg),
        shadowElevation = 2.dp,
        content = content,
    )
}

@Composable
fun LifeMashCard(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit = {},
    color: Color = MaterialTheme.colorScheme.surface,
    content: @Composable () -> Unit,
) {
    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth(),
        color = color,
        shape = RoundedCornerShape(LifeMashRadius.lg),
        shadowElevation = 2.dp,
        content = content,
    )
}
