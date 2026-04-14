package org.bmsk.lifemash.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import org.bmsk.lifemash.designsystem.theme.LifeMashRadius
import org.bmsk.lifemash.designsystem.theme.LifeMashSpacing
import org.bmsk.lifemash.designsystem.theme.LocalLifeMashColors

enum class LifeMashButtonStyle { Primary, Secondary, Ghost, Danger, Outline }
enum class LifeMashButtonSize { Default, Small }

@Composable
fun LifeMashButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: LifeMashButtonStyle = LifeMashButtonStyle.Primary,
    size: LifeMashButtonSize = LifeMashButtonSize.Default,
    enabled: Boolean = true,
    isLoading: Boolean = false,
) {
    val semantic = LocalLifeMashColors.current
    val shape = RoundedCornerShape(LifeMashRadius.md)
    val height = when (size) {
        LifeMashButtonSize.Default -> 48.dp
        LifeMashButtonSize.Small -> 36.dp
    }
    val padding = when (size) {
        LifeMashButtonSize.Default -> PaddingValues(horizontal = LifeMashSpacing.xxl, vertical = LifeMashSpacing.md)
        LifeMashButtonSize.Small -> PaddingValues(horizontal = LifeMashSpacing.lg, vertical = LifeMashSpacing.xs)
    }
    val textStyle = when (size) {
        LifeMashButtonSize.Default -> MaterialTheme.typography.titleSmall
        LifeMashButtonSize.Small -> MaterialTheme.typography.labelLarge
    }

    when (style) {
        LifeMashButtonStyle.Primary -> Button(
            onClick = onClick,
            modifier = modifier.height(height),
            enabled = enabled && !isLoading,
            shape = shape,
            contentPadding = padding,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
        ) {
            ButtonContent(
                text = text,
                textStyle = textStyle,
                isLoading = isLoading,
            )
        }

        LifeMashButtonStyle.Secondary -> Button(
            onClick = onClick,
            modifier = modifier.height(height),
            enabled = enabled && !isLoading,
            shape = shape,
            contentPadding = padding,
            colors = ButtonDefaults.buttonColors(
                containerColor = semantic.chipBg,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
        ) {
            ButtonContent(
                text = text,
                textStyle = textStyle,
                isLoading = isLoading,
            )
        }

        LifeMashButtonStyle.Ghost -> TextButton(
            onClick = onClick,
            modifier = modifier.height(height),
            enabled = enabled && !isLoading,
            shape = shape,
            contentPadding = padding,
        ) {
            ButtonContent(
                text = text,
                textStyle = textStyle,
                isLoading = isLoading,
                textColor = MaterialTheme.colorScheme.primary,
            )
        }

        LifeMashButtonStyle.Danger -> Button(
            onClick = onClick,
            modifier = modifier.height(height),
            enabled = enabled && !isLoading,
            shape = shape,
            contentPadding = padding,
            colors = ButtonDefaults.buttonColors(
                containerColor = semantic.danger,
                contentColor = semantic.onDanger,
            ),
        ) {
            ButtonContent(
                text = text,
                textStyle = textStyle,
                isLoading = isLoading,
            )
        }

        LifeMashButtonStyle.Outline -> OutlinedButton(
            onClick = onClick,
            modifier = modifier.height(height),
            enabled = enabled && !isLoading,
            shape = shape,
            contentPadding = padding,
            border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.outlineVariant),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
        ) {
            ButtonContent(
                text = text,
                textStyle = textStyle,
                isLoading = isLoading,
                textColor = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun ButtonContent(
    text: String,
    textStyle: TextStyle,
    isLoading: Boolean,
    textColor: Color = Color.Unspecified,
) {
    if (isLoading) {
        CircularProgressIndicator(
            modifier = Modifier.size(16.dp),
            strokeWidth = 2.dp,
            color = if (textColor != Color.Unspecified) textColor else LocalContentColor.current,
        )
    } else {
        Text(
            text,
            style = textStyle,
            color = textColor,
        )
    }
}
