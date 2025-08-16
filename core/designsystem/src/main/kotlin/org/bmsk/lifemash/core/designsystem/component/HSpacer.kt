package org.bmsk.lifemash.core.designsystem.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

@Composable
fun HSpacer(
    width: Dp
) {
    Spacer(Modifier.width(width))
}