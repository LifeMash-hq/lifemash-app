package org.bmsk.lifemash.data.network.di

import com.google.firebase.firestore.FirebaseFirestore
import org.bmsk.lifemash.data.network.xml.RssConverterFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import org.bmsk.lifemash.data.network.BASE_URL_GOOGLE
import org.bmsk.lifemash.data.network.BASE_URL_SBS
import org.bmsk.lifemash.data.network.BASE_URL_SEARCH
import org.bmsk.lifemash.data.network.service.GoogleNewsService
import org.bmsk.lifemash.data.network.service.LifeMashFirebaseService
import org.bmsk.lifemash.data.network.service.LifeMashFirebaseServiceImpl
import org.bmsk.lifemash.data.network.service.SbsNewsService
import org.bmsk.lifemash.data.network.service.SearchService
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object ServiceModule {
    @Provides
    @Singleton
    fun providesGoogleNewsService(
        okHttpClientBuilder: OkHttpClient.Builder,
        tikXmlConverterFactory: RssConverterFactory,
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
        tikXmlConverterFactory: RssConverterFactory,
    ): SbsNewsService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL_SBS)
            .client(okHttpClientBuilder.build())
            .addConverterFactory(tikXmlConverterFactory)
            .build()
            .create(SbsNewsService::class.java)
    }

    @Provides
    @Singleton
    fun providesSearchService(
        okHttpClientBuilder: OkHttpClient.Builder,
    ): SearchService {
        val json = Json { ignoreUnknownKeys = true }
        return Retrofit.Builder()
            .baseUrl(BASE_URL_SEARCH)
            .client(okHttpClientBuilder.build())
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(SearchService::class.java)
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
