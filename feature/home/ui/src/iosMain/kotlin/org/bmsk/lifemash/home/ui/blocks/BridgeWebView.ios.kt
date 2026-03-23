package org.bmsk.lifemash.home.ui.blocks

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSURLRequest
import platform.Foundation.NSURL
import platform.WebKit.WKScriptMessage
import platform.WebKit.WKScriptMessageHandlerProtocol
import platform.WebKit.WKUserContentController
import platform.WebKit.WKWebView
import platform.WebKit.WKWebViewConfiguration
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun BridgeWebView(
    url: String,
    tokenProvider: () -> String?,
    modifier: Modifier,
) {
    val nsUrl = remember(url) {
        NSURL(string = url)?.takeIf {
            it.scheme?.lowercase() in listOf("https", "http")
        }
    }

    UIKitView(
        modifier = modifier,
        factory = {
            val config = WKWebViewConfiguration()
            val handler = LifeMashBridgeHandler(tokenProvider)
            config.userContentController.addScriptMessageHandler(handler, name = "LifeMashBridge")
            WKWebView(frame = platform.CoreGraphics.CGRectZero.readValue(), configuration = config).apply {
                nsUrl?.let { loadRequest(NSURLRequest(uRL = it)) }
            }
        },
        update = { webView ->
            nsUrl?.let { webView.loadRequest(NSURLRequest(uRL = it)) }
        },
    )
}

private class LifeMashBridgeHandler(
    private val tokenProvider: () -> String?,
) : NSObject(), WKScriptMessageHandlerProtocol {
    override fun userContentController(
        userContentController: WKUserContentController,
        didReceiveScriptMessage: WKScriptMessage,
    ) {
        // iOS: JS calls window.webkit.messageHandlers.LifeMashBridge.postMessage('getToken')
        // Response is sent back via evaluateJavaScript
        val webView = didReceiveScriptMessage.webView ?: return
        val token = tokenProvider() ?: ""
        webView.evaluateJavaScript("window.__lifemashToken = '$token';", completionHandler = null)
    }
}
