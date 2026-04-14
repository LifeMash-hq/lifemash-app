package org.bmsk.lifemash.data.core.di

import org.bmsk.lifemash.domain.eventdetail.EventDetailRepository
import org.bmsk.lifemash.data.remote.eventdetail.EventDetailApi
import org.bmsk.lifemash.data.core.eventdetail.EventDetailRepositoryImpl
import org.koin.dsl.module

val eventDetailDataModule = module {
    single { EventDetailApi(get()) }
    single<EventDetailRepository> { EventDetailRepositoryImpl(get()) }
}
