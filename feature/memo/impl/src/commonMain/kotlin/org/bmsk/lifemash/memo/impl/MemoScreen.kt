package org.bmsk.lifemash.memo.impl

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.bmsk.lifemash.designsystem.theme.LifeMashSpacing
import org.bmsk.lifemash.domain.memo.Memo

@Composable
internal fun MemoScreen(
    uiState: MemoUiState,
    onBack: () -> Unit = {},
    onSearchQueryChange: (String) -> Unit = {},
    onShowOverlay: (MemoOverlay) -> Unit = {},
) {
    Box(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
        Column(Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = LifeMashSpacing.md),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로가기")
                }
                Text(
                    text = "메모",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f),
                )
            }

            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text("메모 검색") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = LifeMashSpacing.lg)
                    .padding(bottom = LifeMashSpacing.sm),
                singleLine = true,
            )

            if (uiState.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                val pinned = uiState.memos.filter { it.isPinned }
                val unpinned = uiState.memos.filter { !it.isPinned }

                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = LifeMashSpacing.lg),
                    verticalArrangement = Arrangement.spacedBy(LifeMashSpacing.sm),
                ) {
                    if (pinned.isNotEmpty()) {
                        item {
                            Text(
                                text = "고정됨",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = LifeMashSpacing.xs),
                            )
                        }
                        items(pinned, key = { it.id }) { memo ->
                            MemoCard(memo = memo, onClick = { onShowOverlay(MemoOverlay.Detail(memo)) })
                        }
                        item { HorizontalDivider(Modifier.padding(vertical = LifeMashSpacing.xs)) }
                    }
                    if (unpinned.isNotEmpty()) {
                        item {
                            Text(
                                text = "메모",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = LifeMashSpacing.xs),
                            )
                        }
                        items(unpinned, key = { it.id }) { memo ->
                            MemoCard(memo = memo, onClick = { onShowOverlay(MemoOverlay.Detail(memo)) })
                        }
                    }
                    if (pinned.isEmpty() && unpinned.isEmpty()) {
                        item {
                            Box(
                                Modifier.fillMaxWidth().padding(top = LifeMashSpacing.xxxl),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = "메모가 없습니다.\n+ 버튼으로 메모를 추가해보세요.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                    item { Spacer(Modifier.height(LifeMashSpacing.huge)) }
                }
            }
        }

        FloatingActionButton(
            onClick = { onShowOverlay(MemoOverlay.Create()) },
            modifier = Modifier.align(Alignment.BottomEnd).padding(LifeMashSpacing.lg),
        ) {
            Icon(Icons.Filled.Add, contentDescription = "메모 추가")
        }
    }
}

@Composable
private fun MemoCard(memo: Memo, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(LifeMashSpacing.sm))
            .clickable(onClick = onClick)
            .padding(LifeMashSpacing.md),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(LifeMashSpacing.xs),
        ) {
            Text(
                text = memo.title,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f),
            )
            if (memo.isPinned) {
                Icon(
                    Icons.Filled.PushPin,
                    contentDescription = "고정됨",
                    modifier = Modifier.size(LifeMashSpacing.lg),
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
        if (memo.isChecklist) {
            val total = memo.checklistItems.size
            val checked = memo.checklistItems.count { it.isChecked }
            if (total > 0) {
                Text(
                    text = "$checked / $total 완료",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else if (memo.content.isNotBlank()) {
            Text(
                text = memo.content,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
            )
        }
    }
}

// region BottomSheets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MemoCreateBottomSheet(
    editingMemo: Memo?,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (title: String,
    content: String,
    isPinned: Boolean,
    isChecklist: Boolean) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val isEdit = editingMemo != null

    var title by remember { mutableStateOf(editingMemo?.title.orEmpty()) }
    var content by remember { mutableStateOf(editingMemo?.content.orEmpty()) }
    var isPinned by remember { mutableStateOf(editingMemo?.isPinned ?: false) }
    var isChecklist by remember { mutableStateOf(editingMemo?.isChecklist ?: false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = LifeMashSpacing.xxl, vertical = LifeMashSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(LifeMashSpacing.lg),
        ) {
            Text(
                text = if (isEdit) "메모 수정" else "새 메모",
                style = MaterialTheme.typography.titleLarge,
            )

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("제목") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading,
            )

            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("내용 (선택)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 6,
                enabled = !isLoading,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("체크리스트", style = MaterialTheme.typography.bodyLarge)
                Switch(
                    checked = isChecklist,
                    onCheckedChange = { isChecklist = it },
                    enabled = !isLoading,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("상단 고정", style = MaterialTheme.typography.bodyLarge)
                Switch(
                    checked = isPinned,
                    onCheckedChange = { isPinned = it },
                    enabled = !isLoading,
                )
            }

            TextButton(
                onClick = { onSubmit(title, content, isPinned, isChecklist) },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotBlank() && !isLoading,
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(LifeMashSpacing.xl), strokeWidth = LifeMashSpacing.micro)
                    Spacer(Modifier.width(LifeMashSpacing.sm))
                }
                Text(if (isEdit) "수정" else "저장")
            }

            Spacer(Modifier.height(LifeMashSpacing.lg))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MemoDetailBottomSheet(
    memo: Memo,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleChecklistItem: (itemId: String) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showDeleteConfirm by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = LifeMashSpacing.xxl, vertical = LifeMashSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(LifeMashSpacing.md),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = memo.title,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f),
                )
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Filled.Edit, contentDescription = "수정")
                    }
                    IconButton(onClick = { showDeleteConfirm = true }) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = "삭제",
                            tint = MaterialTheme.colorScheme.error,
                        )
                    }
                }
            }

            if (memo.isChecklist) {
                memo.checklistItems.forEach { item ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Checkbox(
                            checked = item.isChecked,
                            onCheckedChange = { onToggleChecklistItem(item.id) },
                        )
                        Text(
                            text = item.content,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (item.isChecked) {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            },
                        )
                    }
                }
            } else if (memo.content.isNotBlank()) {
                Text(
                    text = memo.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }

            Spacer(Modifier.height(LifeMashSpacing.lg))
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("메모 삭제") },
            text = { Text("\"${memo.title}\" 메모를 삭제하시겠습니까?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirm = false
                        onDelete()
                    },
                ) { Text("삭제", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("취소") }
            },
        )
    }
}

// endregion
