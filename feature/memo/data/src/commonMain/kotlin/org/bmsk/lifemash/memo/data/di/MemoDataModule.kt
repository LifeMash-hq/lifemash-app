package org.bmsk.lifemash.memo.data.di

import io.ktor.client.HttpClient
import org.bmsk.lifemash.memo.data.api.MemoApi
import org.bmsk.lifemash.memo.data.repository.MemoRepositoryImpl
import org.bmsk.lifemash.memo.domain.repository.MemoRepository
import org.koin.dsl.module

val memoDataModule = module {
    single { MemoApi(get<HttpClient>()) }
    single<MemoRepository> { MemoRepositoryImpl(get()) }
}
