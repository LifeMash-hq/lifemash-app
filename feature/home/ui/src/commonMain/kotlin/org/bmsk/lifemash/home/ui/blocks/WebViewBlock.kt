package org.bmsk.lifemash.home.ui.blocks

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Fixed height for the in-feed WebView block
private val WEBVIEW_BLOCK_HEIGHT = 400.dp

@Composable
internal fun WebViewBlock(url: String, tokenProvider: () -> String?) {
    BridgeWebView(
        url = url,
        tokenProvider = tokenProvider,
        modifier = Modifier
            .fillMaxWidth()
            .height(WEBVIEW_BLOCK_HEIGHT),
    )
}

@Composable
expect fun BridgeWebView(
    url: String,
    tokenProvider: () -> String?,
    modifier: Modifier,
)
