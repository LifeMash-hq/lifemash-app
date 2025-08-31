package org.bmsk.lifemash.core.designsystem.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.bmsk.lifemash.core.designsystem.theme.LifeMashTheme

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

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun ScrapButtonPreview() {
    LifeMashTheme {
        Row {
            ScrapButton(isScrapped = true, onClick = {})
            ScrapButton(isScrapped = false, onClick = {})
        }
    }
}
