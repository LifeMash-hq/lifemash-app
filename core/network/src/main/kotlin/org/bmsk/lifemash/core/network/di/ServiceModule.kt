package org.bmsk.lifemash.core.network.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.bmsk.lifemash.core.network.service.GoogleNewsService
import org.bmsk.lifemash.core.network.service.NewsClient
import org.bmsk.lifemash.core.network.service.NewsClientImpl
import org.bmsk.lifemash.core.network.service.SbsNewsService
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object ServiceModule {
    @Provides
    @Singleton
    fun providesGoogleNewsService(
        @GoogleRetrofit retrofit: Retrofit,
    ): GoogleNewsService {
        return retrofit.create(GoogleNewsService::class.java)
    }

    @Provides
    @Singleton
    fun providesSbsNewsService(
        @SbsRetrofit retrofit: Retrofit,
    ): SbsNewsService {
        return retrofit.create(SbsNewsService::class.java)
    }
}

@Module
@InstallIn(SingletonComponent::class)
internal abstract class ClientModule {
    @Binds
    abstract fun bindNewsClient(
        dataSource: NewsClientImpl,
    ): NewsClient
}
