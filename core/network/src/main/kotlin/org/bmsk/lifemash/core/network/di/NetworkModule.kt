package org.bmsk.lifemash.core.network.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.bmsk.lifemash.core.network.BASE_URL_GOOGLE
import org.bmsk.lifemash.core.network.BASE_URL_SBS
import org.bmsk.lifemash.core.network.TIMEOUT_CONNECT
import org.bmsk.lifemash.core.network.TIMEOUT_READ
import org.bmsk.lifemash.core.network.TIMEOUT_WRITE
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
internal annotation class SbsRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
internal annotation class GoogleRetrofit

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {

    @Provides
    @Singleton
    fun providesJsonConverterFactory(): MoshiConverterFactory {
        return MoshiConverterFactory.create(
            Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build(),
        )
    }

    @Provides
    @Singleton
    fun providesXmlConverterFactory(): TikXmlConverterFactory {
        return TikXmlConverterFactory.create(
            TikXml.Builder()
                .exceptionOnUnreadXml(false)
                .build(),
        )
    }

    @Provides
    @Singleton
    fun providesHttpLogger(): HttpLoggingInterceptor {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return loggingInterceptor
    }

    @Provides
    @Singleton
    fun providesOkHttpClient(
        logger: HttpLoggingInterceptor,
    ): OkHttpClient.Builder {
        return OkHttpClient.Builder().apply {
            addInterceptor(logger)
            connectTimeout(TIMEOUT_CONNECT, TimeUnit.SECONDS)
            readTimeout(TIMEOUT_READ, TimeUnit.SECONDS)
            writeTimeout(TIMEOUT_WRITE, TimeUnit.SECONDS)
        }
    }

    @Provides
    @Singleton
    @SbsRetrofit
    fun providesSbsRetrofit(
        client: OkHttpClient.Builder,
        xmlConverterFactory: TikXmlConverterFactory,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL_SBS)
            .client(client.build())
            .addConverterFactory(xmlConverterFactory)
            .build()
    }

    @Provides
    @Singleton
    @GoogleRetrofit
    fun providesGoogleRetrofit(
        client: OkHttpClient.Builder,
        xmlConverterFactory: TikXmlConverterFactory,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL_GOOGLE)
            .client(client.build())
            .addConverterFactory(xmlConverterFactory)
            .build()
    }
}
