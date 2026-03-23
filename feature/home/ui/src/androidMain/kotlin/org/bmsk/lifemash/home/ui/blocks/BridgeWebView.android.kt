package org.bmsk.lifemash.home.ui.blocks

import android.net.Uri
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
actual fun BridgeWebView(
    url: String,
    tokenProvider: () -> String?,
    modifier: Modifier,
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?,
                    ): Boolean {
                        val scheme = request?.url?.scheme?.lowercase()
                        return scheme != "https" && scheme != "http"
                    }
                }
                with(settings) {
                    loadWithOverviewMode = true
                    useWideViewPort = true
                    setSupportZoom(true)
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    allowFileAccess = false
                    allowContentAccess = false
                }
                addJavascriptInterface(
                    LifeMashBridge(tokenProvider),
                    "LifeMashBridge",
                )
            }
        },
        update = { webView ->
            val scheme = Uri.parse(url).scheme?.lowercase()
            if (scheme == "https" || scheme == "http") {
                webView.loadUrl(url)
            }
        },
    )
}

private class LifeMashBridge(private val tokenProvider: () -> String?) {
    @JavascriptInterface
    fun getToken(): String = tokenProvider() ?: ""
}
