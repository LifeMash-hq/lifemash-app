package org.bmsk.lifemash.home.data.di

import org.bmsk.lifemash.home.data.repository.BlocksTodayRepositoryImpl
import org.bmsk.lifemash.home.data.repository.HomeLayoutRepositoryImpl
import org.bmsk.lifemash.home.data.repository.MarketplaceRepositoryImpl
import org.bmsk.lifemash.home.domain.repository.BlocksTodayRepository
import org.bmsk.lifemash.home.domain.repository.HomeLayoutRepository
import org.bmsk.lifemash.home.domain.repository.MarketplaceRepository
import org.koin.dsl.module

val homeDataModule = module {
    single<HomeLayoutRepository> { HomeLayoutRepositoryImpl(get()) }
    single<BlocksTodayRepository> { BlocksTodayRepositoryImpl(get()) }
    single<MarketplaceRepository> { MarketplaceRepositoryImpl(get()) }
}
