package org.bmsk.lifemash.assistant.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.bmsk.lifemash.assistant.ui.component.AssistantSettingsSheet
import org.bmsk.lifemash.assistant.ui.component.ChatInput
import org.bmsk.lifemash.assistant.ui.component.ChatMessageBubble
import org.bmsk.lifemash.assistant.ui.component.StreamingBubble
import org.bmsk.lifemash.assistant.ui.component.ToolCallChip

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
internal fun AssistantScreen(
    uiState: AssistantUiState,
    onInputChange: (String) -> Unit,
    onSend: () -> Unit,
    onBack: () -> Unit,
    onToggleSettings: () -> Unit,
    onSaveApiKey: (String) -> Unit,
    onRemoveApiKey: () -> Unit,
    onLoadConversations: () -> Unit,
    onLoadConversation: (String) -> Unit,
    onDeleteConversation: (String) -> Unit,
    onNewConversation: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    LaunchedEffect(uiState.messages.size, uiState.streamingText) {
        val targetIndex = uiState.messages.size + (if (uiState.isStreaming) 1 else 0)
        if (targetIndex > 0) {
            listState.animateScrollToItem(targetIndex - 1)
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = "대화 목록",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp),
                )
                IconButton(
                    onClick = {
                        onNewConversation()
                        scope.launch { drawerState.close() }
                    },
                ) {
                    Icon(Icons.Default.Add, "새 대화")
                }
                HorizontalDivider()

                LaunchedEffect(drawerState.isOpen) {
                    if (drawerState.isOpen) onLoadConversations()
                }

                val conversations = uiState.conversations
                if (conversations != null) {
                    LazyColumn {
                        items(conversations, key = { it.id }) { convo ->
                            ListItem(
                                headlineContent = {
                                    Text(
                                        text = convo.title,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                },
                                modifier = Modifier.clickable {
                                    onLoadConversation(convo.id)
                                    scope.launch { drawerState.close() }
                                },
                            )
                        }
                    }
                }
            }
        },
    ) {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("AI 어시스턴트") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "뒤로가기")
                        }
                    },
                    actions = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, "대화 목록")
                        }
                        IconButton(onClick = onToggleSettings) {
                            Icon(Icons.Default.Settings, "설정")
                        }
                    },
                )
            },
            bottomBar = {
                ChatInput(
                    text = uiState.inputText,
                    onTextChange = onInputChange,
                    onSend = onSend,
                    isStreaming = uiState.isStreaming,
                )
            },
        ) { padding ->
            if (uiState.messages.isEmpty() && !uiState.isStreaming) {
                // Empty state
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp),
                    ) {
                        Text(
                            text = "AI 어시스턴트에게 질문해 보세요",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            listOf(
                                "오늘 일정 알려줘",
                                "이번 달 일정 정리해줘",
                                "내 그룹 목록 보여줘",
                            ).forEach { suggestion ->
                                AssistChip(
                                    onClick = {
                                        onInputChange(suggestion)
                                        onSend()
                                    },
                                    label = { Text(suggestion) },
                                )
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    state = listState,
                ) {
                    items(uiState.messages, key = { it.id }) { message ->
                        ChatMessageBubble(message = message)
                    }

                    if (uiState.activeToolCall != null) {
                        item(key = "tool_call") {
                            ToolCallChip(label = uiState.activeToolCall)
                        }
                    }

                    if (uiState.isStreaming && uiState.streamingText.isNotEmpty()) {
                        item(key = "streaming") {
                            StreamingBubble(text = uiState.streamingText)
                        }
                    }
                }
            }
        }
    }

    if (uiState.showSettings) {
        AssistantSettingsSheet(
            hasApiKey = uiState.hasApiKey,
            usage = uiState.usage,
            onSaveApiKey = onSaveApiKey,
            onRemoveApiKey = onRemoveApiKey,
            onDismiss = onToggleSettings,
        )
    }
}
