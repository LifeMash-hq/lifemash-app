package org.bmsk.lifemash.designsystem.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import org.bmsk.lifemash.designsystem.theme.LifeMashRadius
import org.bmsk.lifemash.designsystem.theme.LifeMashSpacing
import org.bmsk.lifemash.designsystem.theme.LocalLifeMashColors

@Composable
fun LifeMashInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    minLines: Int = 1,
    maxLines: Int = 1,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    val semantic = LocalLifeMashColors.current

    Column(modifier = modifier) {
        if (label != null) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(LifeMashSpacing.xs))
        }
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            isError = isError,
            singleLine = singleLine,
            minLines = minLines,
            maxLines = if (singleLine) 1 else maxLines,
            placeholder = placeholder?.let {
                { Text(it, color = semantic.textDisabled) }
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
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
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
