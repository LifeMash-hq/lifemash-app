package org.bmsk.lifemash.feature.scrap.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import org.bmsk.lifemash.feature.scrap.api.ScrapNavController
import org.bmsk.lifemash.feature.scrap.api.ScrapNavGraph
import org.bmsk.lifemash.feature.scrap.navigation.ScrapNavControllerImpl
import org.bmsk.lifemash.feature.scrap.navigation.ScrapNavGraphImpl

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