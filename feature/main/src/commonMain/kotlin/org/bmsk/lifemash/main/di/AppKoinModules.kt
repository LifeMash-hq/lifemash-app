package org.bmsk.lifemash.main.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import org.bmsk.lifemash.data.core.di.authDataModule
import org.bmsk.lifemash.auth.impl.di.authUiModule
import org.bmsk.lifemash.data.core.di.calendarDataModule
import org.bmsk.lifemash.calendar.impl.di.calendarUiModule
import org.bmsk.lifemash.data.core.di.eventDetailDataModule
import org.bmsk.lifemash.eventdetail.impl.di.eventDetailUiModule
import org.bmsk.lifemash.data.core.di.feedDataModule
import org.bmsk.lifemash.feed.impl.di.feedUiModule
import org.bmsk.lifemash.data.core.di.memoDataModule
import org.bmsk.lifemash.memo.impl.di.memoUiModule
import org.bmsk.lifemash.data.core.di.momentDataModule
import org.bmsk.lifemash.moment.impl.di.momentUiModule
import org.bmsk.lifemash.data.core.di.onboardingDataModule
import org.bmsk.lifemash.onboarding.impl.di.onboardingUiModule
import org.bmsk.lifemash.main.MainViewModel
import org.bmsk.lifemash.data.core.di.notificationDataModule
import org.bmsk.lifemash.notification.impl.di.notificationUiModule
import org.bmsk.lifemash.data.core.di.profileDataModule
import org.bmsk.lifemash.profile.impl.di.profileUiModule
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
    onboardingDataModule,
    onboardingUiModule,
)
