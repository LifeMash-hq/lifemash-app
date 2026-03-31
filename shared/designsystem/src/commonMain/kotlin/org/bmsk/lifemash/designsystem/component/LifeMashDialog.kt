package org.bmsk.lifemash.designsystem.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import org.bmsk.lifemash.designsystem.theme.LifeMashRadius
import org.bmsk.lifemash.designsystem.theme.LifeMashShadow
import org.bmsk.lifemash.designsystem.theme.LifeMashSpacing

@Composable
fun LifeMashDialog(
    title: String,
    message: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    dismissText: String? = null,
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = modifier,
            shape = RoundedCornerShape(LifeMashRadius.xl),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = LifeMashShadow.lg,
        ) {
            Column(modifier = Modifier.padding(LifeMashSpacing.xxl)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                )
                Spacer(modifier = Modifier.height(LifeMashSpacing.sm))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(LifeMashSpacing.xxl))
                Row(modifier = Modifier.align(Alignment.End)) {
                    if (dismissText != null) {
                        LifeMashButton(
                            text = dismissText,
                            onClick = onDismiss,
                            style = LifeMashButtonStyle.Ghost,
                        )
                        Spacer(modifier = Modifier.width(LifeMashSpacing.sm))
                    }
                    LifeMashButton(
                        text = confirmText,
                        onClick = onConfirm,
                    )
                }
            }
        }
    }
}
