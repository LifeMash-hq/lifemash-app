package org.bmsk.lifemash.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.bmsk.lifemash.domain.feed.usecase.FeedUseCaseModule
import org.bmsk.lifemash.domain.scrap.usecase.ScrapUseCaseModule

@Module(includes = [FeedUseCaseModule::class, ScrapUseCaseModule::class])
@InstallIn(SingletonComponent::class)
internal object DomainBridgeModule