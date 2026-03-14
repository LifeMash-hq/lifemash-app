package org.bmsk.lifemash.scrap.ui.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import org.bmsk.lifemash.scrap.api.ScrapNavController
import org.bmsk.lifemash.scrap.api.ScrapNavGraph
import org.bmsk.lifemash.scrap.ui.navigation.ScrapNavControllerImpl
import org.bmsk.lifemash.scrap.ui.navigation.ScrapNavGraphImpl

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