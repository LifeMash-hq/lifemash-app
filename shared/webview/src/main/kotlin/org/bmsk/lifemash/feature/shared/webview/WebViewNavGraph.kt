package org.bmsk.lifemash.feature.shared.webview

import org.bmsk.lifemash.feature.shared.navigation.LifeMashNavGraph

interface WebViewNavGraph : LifeMashNavGraph<WebViewNavGraphInfo>

data class WebViewNavGraphInfo(
    val onShowErrorSnackbar: (Throwable?) -> Unit,
)
