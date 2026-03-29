package org.bmsk.lifemash.feature.designsystem.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.bmsk.lifemash.feature.designsystem.theme.LifeMashRadius
import org.bmsk.lifemash.feature.designsystem.theme.LifeMashSpacing
import org.bmsk.lifemash.feature.designsystem.theme.LocalLifeMashColors

enum class LifeMashButtonStyle { Primary, Secondary, Ghost, Danger }

@Composable
fun LifeMashButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: LifeMashButtonStyle = LifeMashButtonStyle.Primary,
    enabled: Boolean = true,
) {
    val semantic = LocalLifeMashColors.current
    val shape = RoundedCornerShape(LifeMashRadius.md)

    when (style) {
        LifeMashButtonStyle.Primary -> Button(
            onClick = onClick,
            modifier = modifier.height(48.dp),
            enabled = enabled,
            shape = shape,
            contentPadding = PaddingValues(horizontal = LifeMashSpacing.xxl, vertical = LifeMashSpacing.md),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
            ),
        ) {
            Text(text, style = MaterialTheme.typography.titleSmall)
        }

        LifeMashButtonStyle.Secondary -> Button(
            onClick = onClick,
            modifier = modifier.height(48.dp),
            enabled = enabled,
            shape = shape,
            contentPadding = PaddingValues(horizontal = LifeMashSpacing.xxl, vertical = LifeMashSpacing.md),
            colors = ButtonDefaults.buttonColors(
                containerColor = semantic.chipBg,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
        ) {
            Text(text, style = MaterialTheme.typography.titleSmall)
        }

        LifeMashButtonStyle.Ghost -> TextButton(
            onClick = onClick,
            modifier = modifier.height(48.dp),
            enabled = enabled,
            shape = shape,
            contentPadding = PaddingValues(horizontal = LifeMashSpacing.xxl, vertical = LifeMashSpacing.md),
        ) {
            Text(
                text,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
            )
        }

        LifeMashButtonStyle.Danger -> Button(
            onClick = onClick,
            modifier = modifier.height(48.dp),
            enabled = enabled,
            shape = shape,
            contentPadding = PaddingValues(horizontal = LifeMashSpacing.xxl, vertical = LifeMashSpacing.md),
            colors = ButtonDefaults.buttonColors(
                containerColor = semantic.danger,
                contentColor = Color.White,
            ),
        ) {
            Text(text, style = MaterialTheme.typography.titleSmall)
        }
    }
}
