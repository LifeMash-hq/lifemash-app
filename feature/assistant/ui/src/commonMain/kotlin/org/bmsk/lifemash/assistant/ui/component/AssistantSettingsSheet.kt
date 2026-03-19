package org.bmsk.lifemash.assistant.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.bmsk.lifemash.assistant.domain.model.AssistantUsage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AssistantSettingsSheet(
    hasApiKey: Boolean,
    usage: AssistantUsage?,
    onSaveApiKey: (String) -> Unit,
    onRemoveApiKey: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp),
        ) {
            Text(
                text = "설정",
                style = MaterialTheme.typography.titleMedium,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Usage
            if (usage != null) {
                Text(
                    text = "오늘 사용량",
                    style = MaterialTheme.typography.labelLarge,
                )
                Text(
                    text = "${usage.requestCount} / ${usage.dailyLimit} 요청",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp),
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            // API Key
            Text(
                text = "API 키",
                style = MaterialTheme.typography.labelLarge,
            )

            if (hasApiKey) {
                Text(
                    text = "API 키가 등록되어 있습니다. (무제한 사용)",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp),
                )
                OutlinedButton(
                    onClick = onRemoveApiKey,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                ) {
                    Text("API 키 삭제")
                }
            } else {
                Text(
                    text = "직접 API 키를 등록하면 무제한으로 사용할 수 있습니다.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                )

                var apiKeyInput by remember { mutableStateOf("") }

                OutlinedTextField(
                    value = apiKeyInput,
                    onValueChange = { apiKeyInput = it },
                    label = { Text("Claude API 키") },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    singleLine = true,
                )

                Button(
                    onClick = {
                        if (apiKeyInput.isNotBlank()) {
                            onSaveApiKey(apiKeyInput)
                            apiKeyInput = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    enabled = apiKeyInput.isNotBlank(),
                ) {
                    Text("API 키 저장")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
