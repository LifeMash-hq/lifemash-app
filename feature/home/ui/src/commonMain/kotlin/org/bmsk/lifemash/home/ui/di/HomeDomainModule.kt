package org.bmsk.lifemash.home.ui.di

import org.bmsk.lifemash.home.domain.usecase.GetBlocksTodayUseCase
import org.bmsk.lifemash.home.domain.usecase.GetHomeLayoutUseCase
import org.bmsk.lifemash.home.domain.usecase.GetMarketplaceBlocksUseCase
import org.bmsk.lifemash.home.domain.usecase.InstallMarketplaceBlockUseCase
import org.bmsk.lifemash.home.domain.usecase.SaveHomeLayoutUseCase
import org.koin.dsl.module

val homeDomainModule = module {
    factory { GetHomeLayoutUseCase(get()) }
    factory { SaveHomeLayoutUseCase(get()) }
    factory { GetBlocksTodayUseCase(get()) }
    factory { GetMarketplaceBlocksUseCase(get()) }
    factory { InstallMarketplaceBlockUseCase(get()) }
}
