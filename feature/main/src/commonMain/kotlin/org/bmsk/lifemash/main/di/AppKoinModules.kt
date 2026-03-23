package org.bmsk.lifemash.main.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import org.bmsk.lifemash.assistant.data.di.assistantDataModule
import org.bmsk.lifemash.assistant.domain.di.assistantDomainModule
import org.bmsk.lifemash.assistant.ui.di.assistantUiModule
import org.bmsk.lifemash.auth.data.di.authDataModule
import org.bmsk.lifemash.auth.domain.di.authDomainModule
import org.bmsk.lifemash.auth.ui.di.authUiModule
import org.bmsk.lifemash.calendar.data.di.calendarDataModule
import org.bmsk.lifemash.calendar.domain.di.calendarDomainModule
import org.bmsk.lifemash.calendar.ui.di.calendarUiModule
import org.bmsk.lifemash.home.data.di.homeDataModule
import org.bmsk.lifemash.home.ui.di.homeDomainModule
import org.bmsk.lifemash.home.ui.di.homeUiModule
import org.bmsk.lifemash.main.MainViewModel
import org.bmsk.lifemash.notification.domain.di.notificationDomainModule
import org.bmsk.lifemash.notification.ui.di.notificationUiModule
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * @param backendBaseUrl 백엔드 API 기본 URL
 * @param dataStore 플랫폼별 DataStore 인스턴스
 * @param notificationDataModule 플랫폼별로 생성된 notification data Koin 모듈 (Room DB Builder 필요)
 * @param platformModules 플랫폼 전용 Koin 모듈 (ErrorReporter, FCM 등)
 */
fun appKoinModules(
    backendBaseUrl: String,
    dataStore: DataStore<Preferences>,
    notificationDataModule: Module,
    platformModules: List<Module> = emptyList(),
): List<Module> = platformModules + listOf(
    module { single<DataStore<Preferences>> { dataStore } },
    module { viewModel { MainViewModel(get()) } },
    httpClientModule(backendBaseUrl),
    authDomainModule,
    authDataModule(dataStore),
    authUiModule,
    calendarDomainModule,
    calendarDataModule,
    calendarUiModule,
    notificationDomainModule,
    notificationDataModule,
    notificationUiModule,
    assistantDomainModule,
    assistantDataModule,
    assistantUiModule,
    homeDomainModule,
    homeDataModule,
    homeUiModule,
)
