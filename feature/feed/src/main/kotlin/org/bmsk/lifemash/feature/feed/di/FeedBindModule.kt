package org.bmsk.lifemash.feature.feed.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import org.bmsk.lifemash.feature.feed.api.FeedNavController
import org.bmsk.lifemash.feature.feed.api.FeedNavGraph
import org.bmsk.lifemash.feature.feed.navigation.FeedNavControllerImpl
import org.bmsk.lifemash.feature.feed.navigation.FeedNavGraphImpl
import org.bmsk.lifemash.feature.feed.usecase.GetArticlesUseCase
import org.bmsk.lifemash.feature.feed.usecase.GetArticlesUseCaseImpl

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

@Module
@InstallIn(ViewModelComponent::class)
internal abstract class UseCaseBindModule {

    @Binds
    @ViewModelScoped
    abstract fun bindGetArticlesUseCase(
        impl: GetArticlesUseCaseImpl
    ): GetArticlesUseCase
}