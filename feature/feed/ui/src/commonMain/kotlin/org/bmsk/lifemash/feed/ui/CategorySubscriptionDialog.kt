package org.bmsk.lifemash.feed.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.bmsk.lifemash.model.ArticleCategory

@Composable
internal fun CategorySubscriptionDialog(
    subscribedCategories: Set<ArticleCategory>,
    onConfirm: (Set<ArticleCategory>) -> Unit,
    onDismiss: () -> Unit,
) {
    val allCategories = remember {
        ArticleCategory.entries.filter { it != ArticleCategory.ALL }
    }

    var selected by remember {
        mutableStateOf(subscribedCategories.toMutableSet())
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("관심 카테고리") },
        text = {
            LazyColumn {
                items(allCategories) { category ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Checkbox(
                            checked = category in selected,
                            onCheckedChange = { checked ->
                                selected = selected.toMutableSet().apply {
                                    if (checked) add(category) else remove(category)
                                }
                            },
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(text = category.style.label)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(selected) }) { Text("확인") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("취소") }
        },
    )
}
