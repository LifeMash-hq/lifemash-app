package org.bmsk.lifemash

import android.app.Application
import org.bmsk.lifemash.auth.data.di.authDataModule
import org.bmsk.lifemash.auth.domain.di.authDomainModule
import org.bmsk.lifemash.calendar.data.di.calendarDataModule
import org.bmsk.lifemash.calendar.domain.di.calendarDomainModule
import org.bmsk.lifemash.data.network.di.networkKoinModule
import org.bmsk.lifemash.di.koinAppModule
import org.bmsk.lifemash.feed.data.di.feedDataModule
import org.bmsk.lifemash.feed.data.history.db.getReadingHistoryDBBuilder
import org.bmsk.lifemash.feed.domain.di.feedDomainModule
import org.bmsk.lifemash.feed.ui.di.feedUiModule
import org.bmsk.lifemash.history.ui.di.historyUiModule
import org.bmsk.lifemash.notification.data.db.getNotificationKeywordDBBuilder
import org.bmsk.lifemash.notification.data.di.notificationDataModule
import org.bmsk.lifemash.notification.domain.di.notificationDomainModule
import org.bmsk.lifemash.scrap.data.db.getScrapArticleDBBuilder
import org.bmsk.lifemash.scrap.data.di.scrapDataModule
import org.bmsk.lifemash.scrap.domain.di.scrapDomainModule
import org.bmsk.lifemash.scrap.ui.di.scrapUiModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class LifeMashApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@LifeMashApplication)
            modules(
                koinAppModule,
                networkKoinModule,
                calendarDomainModule,
                calendarDataModule,
                authDomainModule,
                authDataModule,
                feedDomainModule,
                feedDataModule(getReadingHistoryDBBuilder(this@LifeMashApplication)),
                feedUiModule,
                scrapDomainModule,
                scrapDataModule(getScrapArticleDBBuilder(this@LifeMashApplication)),
                scrapUiModule,
                historyUiModule,
                notificationDomainModule,
                notificationDataModule(getNotificationKeywordDBBuilder(this@LifeMashApplication)),
            )
        }
    }
}
