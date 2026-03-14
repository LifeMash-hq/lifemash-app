package org.bmsk.lifemash.data.network.di

import org.bmsk.lifemash.data.network.xml.RssConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.bmsk.lifemash.data.network.BuildConfig
import org.bmsk.lifemash.data.network.TIMEOUT_CONNECT
import org.bmsk.lifemash.data.network.TIMEOUT_READ
import org.bmsk.lifemash.data.network.TIMEOUT_WRITE
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {

    @Provides
    @Singleton
    fun providesXmlConverterFactory(): RssConverterFactory = RssConverterFactory.create()

    @Provides
    @Singleton
    fun providesHttpLogger(): HttpLoggingInterceptor {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BASIC
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
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
}
