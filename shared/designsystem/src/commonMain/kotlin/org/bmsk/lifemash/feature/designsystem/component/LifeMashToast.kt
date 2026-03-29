package org.bmsk.lifemash.feature.designsystem.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.bmsk.lifemash.feature.designsystem.theme.LifeMashRadius
import org.bmsk.lifemash.feature.designsystem.theme.LifeMashSpacing
import org.bmsk.lifemash.feature.designsystem.theme.LocalLifeMashColors

enum class ToastStyle { Default, Success, Error }

@Composable
fun LifeMashToast(
    message: String,
    modifier: Modifier = Modifier,
    style: ToastStyle = ToastStyle.Default,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    val semantic = LocalLifeMashColors.current
    val textColor = when (style) {
        ToastStyle.Default -> MaterialTheme.colorScheme.onSurface
        ToastStyle.Success -> semantic.success
        ToastStyle.Error -> semantic.danger
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(LifeMashRadius.md),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = LifeMashSpacing.xs,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = LifeMashSpacing.xl, vertical = LifeMashSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.labelLarge,
                color = textColor,
                modifier = Modifier.weight(1f),
            )
            if (actionLabel != null && onAction != null) {
                TextButton(onClick = onAction) {
                    Text(
                        text = actionLabel,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}
