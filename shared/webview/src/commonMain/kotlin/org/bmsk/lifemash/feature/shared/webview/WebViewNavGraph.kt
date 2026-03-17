package org.bmsk.lifemash.feature.shared.webview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute

fun NavGraphBuilder.webViewNavGraph(navInfo: WebViewNavGraphInfo) {
    composable<WebViewRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<WebViewRoute>()
        WebViewWithSourceBar(url = route.url)
    }
}

@Composable
private fun WebViewWithSourceBar(url: String) {
    val domain = url.substringAfter("://").substringBefore("/")

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = domain,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 16.dp, vertical = 8.dp),
        )
        PlatformWebView(url = url, modifier = Modifier.fillMaxSize())
    }
}
