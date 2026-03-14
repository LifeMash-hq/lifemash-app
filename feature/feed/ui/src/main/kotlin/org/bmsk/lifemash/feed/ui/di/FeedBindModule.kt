package org.bmsk.lifemash.feed.ui.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import org.bmsk.lifemash.feed.api.FeedNavController
import org.bmsk.lifemash.feed.api.FeedNavGraph
import org.bmsk.lifemash.feed.ui.navigation.FeedNavControllerImpl
import org.bmsk.lifemash.feed.ui.navigation.FeedNavGraphImpl

@Module
@InstallIn(ActivityComponent::class)
internal abstract class FeedBindModule {
    @Binds
    abstract fun bindFeedNavController(
        impl: FeedNavControllerImpl
    ): FeedNavController

    @Binds
    abstract fun bindFeedNavGraph(
        impl: FeedNavGraphImpl
    ): FeedNavGraph
}