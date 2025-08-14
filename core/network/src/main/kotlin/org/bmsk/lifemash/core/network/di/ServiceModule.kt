package org.bmsk.lifemash.core.network.di

import com.google.firebase.firestore.FirebaseFirestore
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import org.bmsk.lifemash.core.network.BASE_URL_GOOGLE
import org.bmsk.lifemash.core.network.BASE_URL_SBS
import org.bmsk.lifemash.core.network.service.GoogleNewsService
import org.bmsk.lifemash.core.network.service.LifeMashFirebaseService
import org.bmsk.lifemash.core.network.service.LifeMashFirebaseServiceImpl
import org.bmsk.lifemash.core.network.service.SbsNewsService
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object ServiceModule {
    @Provides
    @Singleton
    fun providesGoogleNewsService(
        okHttpClientBuilder: OkHttpClient.Builder,
        tikXmlConverterFactory: TikXmlConverterFactory,
    ): GoogleNewsService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL_GOOGLE)
            .client(okHttpClientBuilder.build())
            .addConverterFactory(tikXmlConverterFactory)
            .build()
            .create(GoogleNewsService::class.java)
    }

    @Provides
    @Singleton
    fun providesSbsNewsService(
        okHttpClientBuilder: OkHttpClient.Builder,
        tikXmlConverterFactory: TikXmlConverterFactory,
    ): SbsNewsService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL_SBS)
            .client(okHttpClientBuilder.build())
            .addConverterFactory(tikXmlConverterFactory)
            .build()
            .create(SbsNewsService::class.java)
    }
}

@Module
@InstallIn(SingletonComponent::class)
internal object FirebaseModule {
    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()
}

@Module
@InstallIn(SingletonComponent::class)
internal abstract class ServiceBindModule {
    @Binds
    @Singleton
    abstract fun bindLifeMashFirebaseService(
        impl: LifeMashFirebaseServiceImpl,
    ): LifeMashFirebaseService
}