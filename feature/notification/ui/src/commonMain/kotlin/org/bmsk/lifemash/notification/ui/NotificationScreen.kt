package org.bmsk.lifemash.notification.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.PersistentList
import kotlin.time.Clock
import kotlin.time.Instant
import org.bmsk.lifemash.notification.domain.model.NotificationKeyword

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NotificationScreen(
    uiState: NotificationUiState,
    onAddKeyword: (String) -> Unit,
    onRemoveKeyword: (Long) -> Unit,
    onBack: () -> Unit,
) {
    var inputText by rememberSaveable { mutableStateOf("") }

    val onSubmit = {
        if (inputText.isNotBlank()) {
            onAddKeyword(inputText)
            inputText = ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("키워드 알림") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로")
                    }
                },
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier.weight(1f).testTag("keyword_input"),
                    placeholder = { Text("관심 키워드 입력") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { onSubmit() }),
                )
                FilledTonalButton(onClick = { onSubmit() }) {
                    Text("추가")
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            when (uiState) {
                is NotificationUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is NotificationUiState.Empty -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Outlined.Notifications,
                                contentDescription = "등록된 키워드 없음",
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                modifier = Modifier.size(72.dp),
                            )
                            Text(
                                text = "등록된 키워드가 없습니다",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 16.dp),
                            )
                            Text(
                                text = "관심 키워드를 추가하면 새 기사가 올라올 때\n알림을 받을 수 있습니다.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                modifier = Modifier.padding(top = 4.dp),
                            )
                        }
                    }
                }
                is NotificationUiState.Loaded -> {
                    KeywordList(
                        keywords = uiState.keywords,
                        onRemoveKeyword = onRemoveKeyword,
                    )
                }
            }
        }
    }
}

@Composable
private fun KeywordList(
    keywords: PersistentList<NotificationKeyword>,
    onRemoveKeyword: (Long) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding = PaddingValues(vertical = 8.dp),
    ) {
        items(keywords, key = { it.id }) { keyword ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = keyword.keyword.value,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Text(
                        text = formatRelativeTime(keyword.createdAt),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Icon(
                    Icons.Outlined.Close,
                    contentDescription = "삭제",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onRemoveKeyword(keyword.id) },
                )
            }
        }
    }
}

private fun formatRelativeTime(instant: Instant): String {
    val now = Clock.System.now()
    val diff = now - instant
    return when {
        diff.inWholeDays >= 365 -> "${diff.inWholeDays / 365}년 전"
        diff.inWholeDays >= 30 -> "${diff.inWholeDays / 30}개월 전"
        diff.inWholeDays >= 1 -> "${diff.inWholeDays}일 전"
        diff.inWholeHours >= 1 -> "${diff.inWholeHours}시간 전"
        diff.inWholeMinutes >= 1 -> "${diff.inWholeMinutes}분 전"
        else -> "방금 전"
    }
}
