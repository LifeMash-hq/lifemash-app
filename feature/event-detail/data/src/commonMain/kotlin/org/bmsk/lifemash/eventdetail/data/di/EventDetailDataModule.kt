package org.bmsk.lifemash.eventdetail.data.di

import org.bmsk.lifemash.eventdetail.data.api.EventDetailApi
import org.bmsk.lifemash.eventdetail.data.repository.EventDetailRepositoryImpl
import org.bmsk.lifemash.eventdetail.domain.repository.EventDetailRepository
import org.koin.dsl.module

val eventDetailDataModule = module {
    single { EventDetailApi(get()) }
    single<EventDetailRepository> { EventDetailRepositoryImpl(get()) }
}
