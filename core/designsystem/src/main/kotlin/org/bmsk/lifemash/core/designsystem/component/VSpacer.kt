package org.bmsk.lifemash.core.designsystem.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

@Composable
fun VSpacer(
    height: Dp,
) {
    Spacer(Modifier.height(height))
}