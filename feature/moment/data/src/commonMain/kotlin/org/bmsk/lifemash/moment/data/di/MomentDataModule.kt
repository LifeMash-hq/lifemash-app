package org.bmsk.lifemash.moment.data.di

import io.ktor.client.HttpClient
import org.bmsk.lifemash.model.upload.UploadService
import org.bmsk.lifemash.moment.data.UploadServiceImpl
import org.bmsk.lifemash.moment.data.api.MomentApi
import org.bmsk.lifemash.moment.data.api.UploadApi
import org.bmsk.lifemash.moment.data.repository.MomentRepositoryImpl
import org.bmsk.lifemash.moment.domain.repository.MomentRepository
import org.koin.core.qualifier.named
import org.koin.dsl.module

val momentDataModule = module {
    single { MomentApi(get<HttpClient>()) }
    // presigned URL 요청은 백엔드로 보내므로 인증된 기본 HttpClient 사용
    single { UploadApi(get<HttpClient>()) }
    single<MomentRepository> { MomentRepositoryImpl(get()) }
    single<UploadService> { UploadServiceImpl(get(), get<HttpClient>(named("upload"))) }
}
