package org.bmsk.lifemash.feature.topic.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import org.bmsk.lifemash.feature.topic.api.TopicNavController
import org.bmsk.lifemash.feature.topic.api.TopicNavGraph
import org.bmsk.lifemash.feature.topic.navigation.TopicNavControllerImpl
import org.bmsk.lifemash.feature.topic.navigation.TopicNavGraphImpl
import org.bmsk.lifemash.feature.topic.usecase.GetGoogleNewsUseCase
import org.bmsk.lifemash.feature.topic.usecase.GetGoogleNewsUseCaseImpl
import org.bmsk.lifemash.feature.topic.usecase.GetNewsImageUrlUseCase
import org.bmsk.lifemash.feature.topic.usecase.GetNewsImageUrlUseCaseImpl
import org.bmsk.lifemash.feature.topic.usecase.GetNewsWithImagesUseCase
import org.bmsk.lifemash.feature.topic.usecase.GetNewsWithImagesUseCaseImpl
import org.bmsk.lifemash.feature.topic.usecase.GetSBSNewsUseCase
import org.bmsk.lifemash.feature.topic.usecase.GetSBSNewsUseCaseImpl
import org.bmsk.lifemash.feature.topic.usecase.ScrapNewsUseCase
import org.bmsk.lifemash.feature.topic.usecase.ScrapNewsUseCaseImpl

@Module
@InstallIn(ActivityComponent::class)
internal abstract class TopicBindModule {

    @Binds
    abstract fun bindTopicNavController(
        dataSource: TopicNavControllerImpl
    ): TopicNavController

    @Binds
    abstract fun bindTopicNavGraph(
        dataSource: TopicNavGraphImpl
    ): TopicNavGraph
}

@Module
@InstallIn(ViewModelComponent::class)
internal abstract class UseCaseBindModule {
    @Binds
    @ViewModelScoped
    abstract fun scrapNewsUseCase(
        dataSource: ScrapNewsUseCaseImpl
    ): ScrapNewsUseCase

    @Binds
    @ViewModelScoped
    abstract fun getGoogleNewsUseCase(
        dataSource: GetGoogleNewsUseCaseImpl
    ): GetGoogleNewsUseCase

    @Binds
    @ViewModelScoped
    abstract fun getSBSNewsUseCase(
        dataSource: GetSBSNewsUseCaseImpl
    ): GetSBSNewsUseCase

    @Binds
    @ViewModelScoped
    abstract fun getNewsImageUrlUseCase(
        dataSource: GetNewsImageUrlUseCaseImpl
    ): GetNewsImageUrlUseCase

    @Binds
    @ViewModelScoped
    abstract fun getNewsWithImagesUseCase(
        dataSource: GetNewsWithImagesUseCaseImpl
    ): GetNewsWithImagesUseCase
}