package org.bmsk.lifemash.feed.impl

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.bmsk.lifemash.designsystem.component.LifeMashAvatar
import org.bmsk.lifemash.designsystem.component.LifeMashInput
import org.bmsk.lifemash.domain.feed.FeedComment
import org.bmsk.lifemash.designsystem.theme.LifeMashSpacing
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CommentSheet(
    postId: String,
    viewModel: FeedViewModel,
    onDismiss: () -> Unit,
) {
    val comments by viewModel.comments.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(postId) {
        viewModel.loadComments(postId)
    }

    ModalBottomSheet(
        onDismissRequest = {
            viewModel.clearComments()
            onDismiss()
        },
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .navigationBarsPadding()
                .imePadding(),
        ) {
            Text(
                text = "댓글",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(
                    horizontal = LifeMashSpacing.lg,
                    vertical = LifeMashSpacing.md,
                ),
            )
            HorizontalDivider()

            if (comments.isEmpty()) {
                Text(
                    text = "아직 댓글이 없습니다. 첫 댓글을 남겨보세요!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .padding(LifeMashSpacing.xl),
                )
            } else {
                LazyColumn(modifier = Modifier.weight(1f, fill = false)) {
                    items(comments) { comment ->
                        CommentItem(comment)
                    }
                }
            }

            HorizontalDivider()
            CommentInputBar(
                onSubmit = { content ->
                    viewModel.submitComment(postId, content)
                },
            )
        }
    }
}

@Composable
private fun CommentItem(comment: FeedComment) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = LifeMashSpacing.lg, vertical = LifeMashSpacing.sm),
        verticalAlignment = Alignment.Top,
    ) {
        LifeMashAvatar(
            name = comment.authorNickname,
            imageUrl = comment.authorProfileImage,
        )
        Spacer(modifier = Modifier.width(LifeMashSpacing.sm))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = comment.authorNickname,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = comment.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (comment.createdAt.isNotEmpty()) {
                Text(
                    text = comment.createdAt,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun CommentInputBar(onSubmit: (String) -> Unit) {
    var text by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = LifeMashSpacing.md, vertical = LifeMashSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LifeMashInput(
            value = text,
            onValueChange = { text = it },
            placeholder = "댓글 입력...",
            modifier = Modifier.weight(1f),
        )
        Spacer(modifier = Modifier.width(LifeMashSpacing.xs))
        IconButton(
            onClick = {
                val trimmed = text.trim()
                if (trimmed.isNotEmpty()) {
                    onSubmit(trimmed)
                    text = ""
                }
            },
            modifier = Modifier.size(LifeMashSpacing.xxxl),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "댓글 전송",
                tint = if (text.isNotBlank()) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
