package org.bmsk.lifemash_newsapp

import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

object Retrofits {
    val sbsRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL_SBS)
        .addConverterFactory(
            TikXmlConverterFactory.create(
                TikXml.Builder()
                    .exceptionOnUnreadXml(false)    // 필요한 데이터만 골라서 매핑할 것이기 때문에 이 속성을 추가
                    .build()
            )
        )
        .client(OkHttpClient.Builder().apply {
            addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
        }.build())
        .build()

    val googleRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL_GOOGLE)
        .addConverterFactory(
            TikXmlConverterFactory.create(
                TikXml.Builder()
                    .exceptionOnUnreadXml(false)    // 필요한 데이터만 골라서 매핑할 것이기 때문에 이 속성을 추가
                    .build()
            )
        )
        .client(OkHttpClient.Builder().apply {
            addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
        }.build())
        .build()
}