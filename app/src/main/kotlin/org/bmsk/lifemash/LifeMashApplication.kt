package org.bmsk.lifemash

import android.app.Application
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import okio.Path.Companion.toPath
import org.bmsk.lifemash.auth.data.di.authDataModule
import org.bmsk.lifemash.auth.domain.di.authDomainModule
import org.bmsk.lifemash.calendar.data.di.calendarDataModule
import org.bmsk.lifemash.calendar.domain.di.calendarDomainModule
import org.bmsk.lifemash.data.network.di.networkKoinModule
import org.bmsk.lifemash.di.koinAppModule
import org.bmsk.lifemash.notification.data.db.getNotificationKeywordDBBuilder
import org.bmsk.lifemash.notification.data.di.notificationDataModule
import org.bmsk.lifemash.notification.domain.di.notificationDomainModule
import org.bmsk.lifemash.notification.ui.di.notificationUiModule
import org.bmsk.lifemash.assistant.data.di.assistantDataModule
import org.bmsk.lifemash.assistant.domain.di.assistantDomainModule
import org.bmsk.lifemash.assistant.ui.di.assistantUiModule
import org.bmsk.lifemash.auth.ui.di.authUiModule
import org.bmsk.lifemash.home.data.di.homeDataModule
import org.bmsk.lifemash.home.ui.di.homeDomainModule
import org.bmsk.lifemash.home.ui.di.homeUiModule
import org.bmsk.lifemash.calendar.ui.di.calendarUiModule
import org.bmsk.lifemash.error.CrashlyticsErrorReporter
import org.bmsk.lifemash.feature.shared.error.ErrorReporter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.bmsk.lifemash.fcm.PushNotificationService
import org.bmsk.lifemash.notification.domain.usecase.SyncKeywordsUseCase
import com.kakao.sdk.common.KakaoSdk
import org.bmsk.lifemash.main.MainViewModel
import org.koin.core.module.dsl.viewModel
import java.io.IOException

class LifeMashApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)

        val dataStore = PreferenceDataStoreFactory.createWithPath {
            filesDir.resolve("datastore/lifemash_prefs.preferences_pb").absolutePath.toPath()
        }

        startKoin {
            androidContext(this@LifeMashApplication)
            modules(
                module { single { PushNotificationService() } },
                module { single { dataStore } },
                module { single<ErrorReporter> { CrashlyticsErrorReporter() } },
                koinAppModule,
                networkKoinModule,
                calendarDomainModule,
                calendarDataModule,
                authDomainModule,
                authDataModule(dataStore),
                notificationDomainModule,
                notificationDataModule(getNotificationKeywordDBBuilder(this@LifeMashApplication)),
                notificationUiModule,
                authUiModule,
                module { viewModel { MainViewModel(get()) } },
                calendarUiModule,
                assistantDomainModule,
                assistantDataModule,
                assistantUiModule,
                homeDomainModule,
                homeDataModule,
                homeUiModule,
            )
        }

        syncFcmToken()
    }

    private fun syncFcmToken() {
        val pushService: PushNotificationService = get()
        val syncUseCase: SyncKeywordsUseCase = get()
        val errorReporter: ErrorReporter = get()

        pushService.registerToken { token ->
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    syncUseCase(token)
                } catch (e: IOException) {
                    errorReporter.log("FCM token sync: network unavailable")
                } catch (e: Exception) {
                    errorReporter.report(e)
                }
            }
        }
    }
}
