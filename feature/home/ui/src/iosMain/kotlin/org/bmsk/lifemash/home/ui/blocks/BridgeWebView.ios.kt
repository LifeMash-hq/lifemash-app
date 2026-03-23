package org.bmsk.lifemash.home.ui.blocks

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.cValue
import platform.CoreGraphics.CGRectMake
import platform.Foundation.NSURLRequest
import platform.Foundation.NSURL
import platform.WebKit.WKNavigationAction
import platform.WebKit.WKNavigationActionPolicy
import platform.WebKit.WKNavigationDelegateProtocol
import platform.WebKit.WKScriptMessage
import platform.WebKit.WKScriptMessageHandlerProtocol
import platform.WebKit.WKUserContentController
import platform.WebKit.WKWebView
import platform.WebKit.WKWebViewConfiguration
import platform.UIKit.UIApplication
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
            WKWebView(frame = CGRectMake(0.0, 0.0, 0.0, 0.0), configuration = config).apply {
                navigationDelegate = ExternalLinkNavigationDelegate()
                nsUrl?.let { loadRequest(NSURLRequest(uRL = it)) }
            }
        },
    )
}

private class ExternalLinkNavigationDelegate : NSObject(), WKNavigationDelegateProtocol {
    override fun webView(
        webView: WKWebView,
        decidePolicyForNavigationAction: WKNavigationAction,
        decisionHandler: (WKNavigationActionPolicy) -> Unit,
    ) {
        val request = decidePolicyForNavigationAction.request
        val targetUrl = request.URL
        val isMainFrame = decidePolicyForNavigationAction.targetFrame?.isMainFrame() == true

        if (!isMainFrame && targetUrl != null) {
            UIApplication.sharedApplication.openURL(targetUrl)
            decisionHandler(WKNavigationActionPolicy.WKNavigationActionPolicyCancel)
            return
        }
        decisionHandler(WKNavigationActionPolicy.WKNavigationActionPolicyAllow)
    }
}

private class LifeMashBridgeHandler(
    private val tokenProvider: () -> String?,
) : NSObject(), WKScriptMessageHandlerProtocol {
    override fun userContentController(
        userContentController: WKUserContentController,
        didReceiveScriptMessage: WKScriptMessage,
    ) {
        val webView = didReceiveScriptMessage.webView ?: return
        val token = tokenProvider() ?: ""
        val escaped = token.replace("\\", "\\\\").replace("'", "\\'")
        webView.evaluateJavaScript("window.__lifemashToken = '$escaped';", completionHandler = null)
    }
}
