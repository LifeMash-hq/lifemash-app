package org.bmsk.lifemash.home.ui.blocks

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun BridgeWebView(
    url: String,
    tokenProvider: () -> String?,
    modifier: Modifier,
)
