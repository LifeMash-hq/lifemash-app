package org.bmsk.lifemash.data.core.di

import io.ktor.client.HttpClient
import org.bmsk.lifemash.domain.memo.MemoRepository
import org.bmsk.lifemash.data.remote.memo.MemoApi
import org.bmsk.lifemash.data.core.memo.MemoRepositoryImpl
import org.koin.dsl.module

val memoDataModule = module {
    single { MemoApi(get<HttpClient>()) }
    single<MemoRepository> { MemoRepositoryImpl(get()) }
}
