package org.bmsk.lifemash.feature.shared.webview

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSURLRequest
import platform.Foundation.NSURL

private fun isAllowedUrl(url: String): Boolean {
    val scheme = url.substringBefore("://").lowercase()
    return scheme == "https" || scheme == "http"
}

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun PlatformWebView(url: String, modifier: Modifier) {
    UIKitView(
        modifier = modifier,
        factory = {
            platform.WebKit.WKWebView().apply {
                if (isAllowedUrl(url)) {
                    loadRequest(NSURLRequest(uRL = NSURL(string = url)!!))
                }
            }
        },
        update = { webView ->
            if (isAllowedUrl(url)) {
                webView.loadRequest(NSURLRequest(uRL = NSURL(string = url)!!))
            }
        },
    )
}
