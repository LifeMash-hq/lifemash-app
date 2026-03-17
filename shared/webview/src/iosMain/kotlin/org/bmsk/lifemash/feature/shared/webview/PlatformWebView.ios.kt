package org.bmsk.lifemash.feature.shared.webview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSURLRequest
import platform.Foundation.NSURL

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun PlatformWebView(url: String, modifier: Modifier) {
    val nsUrl = remember(url) {
        NSURL(string = url)?.takeIf {
            it.scheme?.lowercase() in listOf("https", "http")
        }
    }

    UIKitView(
        modifier = modifier,
        factory = {
            platform.WebKit.WKWebView().apply {
                nsUrl?.let { loadRequest(NSURLRequest(uRL = it)) }
            }
        },
        update = { webView ->
            nsUrl?.let { webView.loadRequest(NSURLRequest(uRL = it)) }
        },
    )
}
