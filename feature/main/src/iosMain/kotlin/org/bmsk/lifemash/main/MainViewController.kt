package org.bmsk.lifemash.main

import androidx.compose.ui.window.ComposeUIViewController
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import okio.Path.Companion.toPath
import org.bmsk.lifemash.auth.data.di.authDataModule
import org.bmsk.lifemash.auth.domain.di.authDomainModule
import org.bmsk.lifemash.calendar.data.di.calendarDataModule
import org.bmsk.lifemash.calendar.domain.di.calendarDomainModule
import org.bmsk.lifemash.data.network.di.iosNetworkKoinModule
import org.bmsk.lifemash.feature.designsystem.theme.LifeMashTheme
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
import org.koin.core.context.startKoin
import org.koin.dsl.module
import platform.Foundation.NSHomeDirectory

private val iosAppModule = module {
    single<HttpClient> {
        HttpClient(Darwin) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true; isLenient = true })
            }
            install(Logging) { level = LogLevel.BODY }
        }
    }
    single<DataStore<Preferences>> {
        PreferenceDataStoreFactory.createWithPath {
            (NSHomeDirectory() + "/Documents/lifemash_prefs.preferences_pb").toPath()
        }
    }
}

fun initKoin() {
    startKoin {
        modules(
            iosAppModule,
            iosNetworkKoinModule,
            feedDomainModule,
            feedDataModule(getReadingHistoryDBBuilder()),
            feedUiModule,
            scrapDomainModule,
            scrapDataModule(getScrapArticleDBBuilder()),
            scrapUiModule,
            historyUiModule,
            authDomainModule,
            authDataModule,
            calendarDomainModule,
            calendarDataModule,
            notificationDomainModule,
            notificationDataModule(getNotificationKeywordDBBuilder()),
        )
    }
}

fun MainViewController() = ComposeUIViewController {
    LifeMashTheme {
        MainScreen()
    }
}
