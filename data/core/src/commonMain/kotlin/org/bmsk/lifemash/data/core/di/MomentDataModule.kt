package org.bmsk.lifemash.data.core.di

import io.ktor.client.HttpClient
import org.bmsk.lifemash.domain.moment.MomentRepository
import org.bmsk.lifemash.domain.moment.UploadService
import org.bmsk.lifemash.data.core.moment.UploadServiceImpl
import org.bmsk.lifemash.data.remote.moment.MomentApi
import org.bmsk.lifemash.data.remote.moment.UploadApi
import org.bmsk.lifemash.data.core.moment.MomentRepositoryImpl
import org.koin.core.qualifier.named
import org.koin.dsl.module

val momentDataModule = module {
    single { MomentApi(get<HttpClient>()) }
    single { UploadApi(get<HttpClient>()) }
    single<MomentRepository> { MomentRepositoryImpl(get()) }
    single<UploadService> { UploadServiceImpl(get(), get<HttpClient>(named("upload"))) }
}
