package org.bmsk.lifemash.home.ui.blocks

import android.content.Intent
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
                        val requestUrl = request?.url ?: return true
                        val scheme = requestUrl.scheme?.lowercase()
                        if (scheme == "https" || scheme == "http") {
                            context.startActivity(Intent(Intent.ACTION_VIEW, requestUrl))
                        }
                        return true
                    }
                }
                with(settings) {
                    loadWithOverviewMode = true
                    useWideViewPort = true
                    setSupportZoom(false)
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    allowFileAccess = false
                    allowContentAccess = false
                }
                isNestedScrollingEnabled = true
                addJavascriptInterface(
                    LifeMashBridge(tokenProvider),
                    "LifeMashBridge",
                )
                val scheme = Uri.parse(url).scheme?.lowercase()
                if (scheme == "https" || scheme == "http") {
                    loadUrl(url)
                }
            }
        },
    )
}

private class LifeMashBridge(private val tokenProvider: () -> String?) {
    @JavascriptInterface
    fun getToken(): String = tokenProvider() ?: ""
}
