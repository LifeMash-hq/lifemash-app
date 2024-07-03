package org.bmsk.lifemash.feature.topic.api

import org.bmsk.lifemash.feature.nav.LifeMashNavGraph

interface WebViewNavGraph : LifeMashNavGraph<WebViewNavGraphInfo>

data class WebViewNavGraphInfo(
    val onShowErrorSnackbar: (Throwable?) -> Unit,
)
