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
import org.bmsk.lifemash.assistant.data.di.assistantDataModule
import org.bmsk.lifemash.assistant.domain.di.assistantDomainModule
import org.bmsk.lifemash.assistant.ui.di.assistantUiModule
import org.bmsk.lifemash.auth.ui.di.authUiModule
import org.bmsk.lifemash.calendar.ui.di.calendarUiModule
import org.bmsk.lifemash.home.data.di.homeDataModule
import org.bmsk.lifemash.home.ui.di.homeDomainModule
import org.bmsk.lifemash.home.ui.di.homeUiModule
import org.bmsk.lifemash.notification.data.db.getNotificationKeywordDBBuilder
import org.bmsk.lifemash.notification.data.di.notificationDataModule
import org.bmsk.lifemash.notification.domain.di.notificationDomainModule
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import platform.Foundation.NSHomeDirectory

private val iosDataStore: DataStore<Preferences> = PreferenceDataStoreFactory.createWithPath {
    (NSHomeDirectory() + "/Documents/lifemash_prefs.preferences_pb").toPath()
}

private val iosAppModule = module {
    single<HttpClient> {
        HttpClient(Darwin) {
            engine {
                configureRequest {
                    setTimeoutInterval(90.0)
                }
            }
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true; isLenient = true })
            }
            install(Logging) { level = LogLevel.BODY }
        }
    }
    single<DataStore<Preferences>> { iosDataStore }
}

fun initKoin() {
    startKoin {
        modules(
            iosAppModule,
            iosNetworkKoinModule,
            authDomainModule,
            authDataModule(iosDataStore),
            calendarDomainModule,
            calendarDataModule,
            notificationDomainModule,
            notificationDataModule(getNotificationKeywordDBBuilder()),
            authUiModule,
            calendarUiModule,
            assistantDomainModule,
            assistantDataModule,
            assistantUiModule,
            homeDomainModule,
            homeDataModule,
            homeUiModule,
            module { viewModel { MainViewModel(get()) } },
        )
    }
}

fun MainViewController() = ComposeUIViewController {
    LifeMashTheme {
        MainScreen()
    }
}
