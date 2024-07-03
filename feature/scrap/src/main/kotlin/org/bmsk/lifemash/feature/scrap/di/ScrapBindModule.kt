package org.bmsk.lifemash.feature.scrap.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import org.bmsk.lifemash.feature.scrap.api.ScrapNavController
import org.bmsk.lifemash.feature.scrap.api.ScrapNavGraph
import org.bmsk.lifemash.feature.scrap.navigation.ScrapNavControllerImpl
import org.bmsk.lifemash.feature.scrap.navigation.ScrapNavGraphImpl
import org.bmsk.lifemash.feature.scrap.usecase.DeleteScrapNewsUseCase
import org.bmsk.lifemash.feature.scrap.usecase.DeleteScrapNewsUseCaseImpl
import org.bmsk.lifemash.feature.scrap.usecase.GetScrapNewsUseCase
import org.bmsk.lifemash.feature.scrap.usecase.GetScrapNewsUseCaseImpl

@Module
@InstallIn(ActivityComponent::class)
internal abstract class ScrapBindModule {
    @Binds
    abstract fun scrapNavControllerImpl(
        dataSource: ScrapNavControllerImpl
    ): ScrapNavController

    @Binds
    abstract fun scrapNavGraphImpl(
        dataSource: ScrapNavGraphImpl
    ): ScrapNavGraph
}

@Module
@InstallIn(ViewModelComponent::class)
internal abstract class UseCaseBindModule {
    @ViewModelScoped
    @Binds
    abstract fun getScrapNewsUseCase(
        dataSource: GetScrapNewsUseCaseImpl
    ): GetScrapNewsUseCase

    @ViewModelScoped
    @Binds
    abstract fun deleteScrapNewsUseCase(
        dataSource: DeleteScrapNewsUseCaseImpl
    ): DeleteScrapNewsUseCase
}