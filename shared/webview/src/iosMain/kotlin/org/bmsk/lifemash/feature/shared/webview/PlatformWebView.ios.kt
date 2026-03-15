package org.bmsk.lifemash.feature.shared.webview

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSURLRequest
import platform.Foundation.NSURL
import platform.WebKit.WKWebView

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun PlatformWebView(url: String, modifier: Modifier) {
    UIKitView(
        modifier = modifier,
        factory = {
            WKWebView().apply {
                loadRequest(NSURLRequest(uRL = NSURL(string = url)!!))
            }
        },
        update = { webView ->
            webView.loadRequest(NSURLRequest(uRL = NSURL(string = url)!!))
        },
    )
}
