package org.bmsk.lifemash.feature.shared.webview.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import org.bmsk.lifemash.feature.shared.webview.navigation.WebViewNavControllerImpl
import org.bmsk.lifemash.feature.shared.webview.navigation.WebViewNavGraphImpl
import org.bmsk.lifemash.feature.shared.webview.WebViewNavController
import org.bmsk.lifemash.feature.shared.webview.WebViewNavGraph

@Module
@InstallIn(ActivityComponent::class)
internal abstract class WebViewBindModule {

    @Binds
    abstract fun bindWebViewNavController(
        dataSource: WebViewNavControllerImpl
    ): WebViewNavController

    @Binds
    abstract fun bindWebViewNavGraph(
        dataSource: WebViewNavGraphImpl
    ): WebViewNavGraph
}