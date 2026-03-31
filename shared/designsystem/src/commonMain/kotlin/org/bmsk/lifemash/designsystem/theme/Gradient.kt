package org.bmsk.lifemash.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush

object LifeMashGradient {
    @Composable
    fun primaryBrush(): Brush {
        val primary = MaterialTheme.colorScheme.primary
        val primaryDark = LocalLifeMashColors.current.primaryDark
        return Brush.linearGradient(colors = listOf(primary, primaryDark))
    }
}
