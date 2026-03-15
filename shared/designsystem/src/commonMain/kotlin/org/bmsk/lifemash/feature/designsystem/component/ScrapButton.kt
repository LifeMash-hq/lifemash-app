package org.bmsk.lifemash.feature.designsystem.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ScrapButton(
    isScrapped: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(onClick = onClick, modifier = modifier) {
        val icon = if (isScrapped) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder
        val tint = if (isScrapped) {
            MaterialTheme.colorScheme.secondary
        } else {
            MaterialTheme.colorScheme.outline
        }
        Icon(
            imageVector = icon,
            contentDescription = if (isScrapped) "Unsave" else "Save",
            tint = tint
        )
    }
}
