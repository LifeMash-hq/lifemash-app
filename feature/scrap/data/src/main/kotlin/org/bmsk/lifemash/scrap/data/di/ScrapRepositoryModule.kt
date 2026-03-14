package org.bmsk.lifemash.scrap.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.bmsk.lifemash.scrap.data.repository.ScrapRepositoryImpl
import org.bmsk.lifemash.scrap.domain.repository.ScrapRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class ScrapRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindScrapRepository(
        impl: ScrapRepositoryImpl,
    ): ScrapRepository
}
