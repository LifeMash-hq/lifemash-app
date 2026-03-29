package org.bmsk.lifemash.feature.designsystem.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.bmsk.lifemash.feature.designsystem.theme.LifeMashRadius
import org.bmsk.lifemash.feature.designsystem.theme.LifeMashSpacing
import org.bmsk.lifemash.feature.designsystem.theme.LocalLifeMashColors

@Composable
fun LifeMashInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    enabled: Boolean = true,
) {
    val semantic = LocalLifeMashColors.current

    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            isError = isError,
            placeholder = placeholder?.let {
                { Text(it, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)) }
            },
            shape = RoundedCornerShape(LifeMashRadius.md),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = semantic.inputBg,
                focusedContainerColor = semantic.inputBg,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                errorBorderColor = semantic.danger,
            ),
            textStyle = MaterialTheme.typography.bodyMedium,
        )
        if (isError && errorMessage != null) {
            Spacer(modifier = Modifier.height(LifeMashSpacing.xxs))
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.labelSmall,
                color = semantic.danger,
            )
        }
    }
}
