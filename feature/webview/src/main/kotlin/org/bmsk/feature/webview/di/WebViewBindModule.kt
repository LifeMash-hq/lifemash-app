package org.bmsk.feature.webview.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import org.bmsk.feature.webview.navigation.WebViewNavControllerImpl
import org.bmsk.feature.webview.navigation.WebViewNavGraphImpl
import org.bmsk.lifemash.feature.topic.api.WebViewNavController
import org.bmsk.lifemash.feature.topic.api.WebViewNavGraph

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