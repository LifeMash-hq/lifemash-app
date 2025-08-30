package org.bmsk.lifemash.feature.feed.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import org.bmsk.lifemash.feature.feed.api.FeedNavController
import org.bmsk.lifemash.feature.feed.api.FeedNavGraph
import org.bmsk.lifemash.feature.feed.navigation.FeedNavControllerImpl
import org.bmsk.lifemash.feature.feed.navigation.FeedNavGraphImpl

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