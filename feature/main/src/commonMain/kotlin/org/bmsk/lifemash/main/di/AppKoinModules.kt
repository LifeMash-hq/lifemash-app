package org.bmsk.lifemash.main.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import org.bmsk.lifemash.auth.data.di.authDataModule
import org.bmsk.lifemash.auth.ui.di.authUiModule
import org.bmsk.lifemash.calendar.data.di.calendarDataModule
import org.bmsk.lifemash.calendar.ui.di.calendarUiModule
import org.bmsk.lifemash.eventdetail.data.di.eventDetailDataModule
import org.bmsk.lifemash.eventdetail.ui.di.eventDetailUiModule
import org.bmsk.lifemash.feed.data.di.feedDataModule
import org.bmsk.lifemash.feed.ui.di.feedUiModule
import org.bmsk.lifemash.memo.data.di.memoDataModule
import org.bmsk.lifemash.memo.ui.di.memoUiModule
import org.bmsk.lifemash.moment.data.di.momentDataModule
import org.bmsk.lifemash.moment.ui.di.momentUiModule
import org.bmsk.lifemash.main.MainViewModel
import org.bmsk.lifemash.notification.data.di.notificationDataModule
import org.bmsk.lifemash.notification.ui.di.notificationUiModule
import org.bmsk.lifemash.profile.data.di.profileDataModule
import org.bmsk.lifemash.profile.ui.di.profileUiModule
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * @param backendBaseUrl 백엔드 API 기본 URL
 * @param dataStore 플랫폼별 DataStore 인스턴스
 * @param platformModules 플랫폼 전용 Koin 모듈 (ErrorReporter, FCM 등)
 */
fun appKoinModules(
    backendBaseUrl: String,
    dataStore: DataStore<Preferences>,
    platformModules: List<Module> = emptyList(),
): List<Module> = platformModules + listOf(
    module { single<DataStore<Preferences>> { dataStore } },
    module { viewModel { MainViewModel(get()) } },
    httpClientModule(backendBaseUrl),
    authDataModule(dataStore),
    authUiModule,
    calendarDataModule,
    calendarUiModule,
    notificationDataModule,
    notificationUiModule,
    profileDataModule,
    profileUiModule,
    feedDataModule,
    feedUiModule,
    eventDetailDataModule,
    eventDetailUiModule,
    memoDataModule,
    memoUiModule,
    momentDataModule,
    momentUiModule,
)
