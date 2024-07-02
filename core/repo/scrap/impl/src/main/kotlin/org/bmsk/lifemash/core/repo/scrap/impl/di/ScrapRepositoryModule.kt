package org.bmsk.lifemash.core.repo.scrap.impl.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.components.SingletonComponent
import org.bmsk.lifemash.core.repo.scrap.api.ScrapNewsRepository
import org.bmsk.lifemash.core.repo.scrap.impl.ScrapNewsRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
internal abstract class ScrapRepositoryModule {
    @Binds
    abstract fun bindScrapNewsRepository(
        scrapNewsRepositoryImpl: ScrapNewsRepositoryImpl
    ): ScrapNewsRepository
}