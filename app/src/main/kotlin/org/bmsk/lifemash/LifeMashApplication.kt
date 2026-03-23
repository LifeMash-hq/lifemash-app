package org.bmsk.lifemash

import android.app.Application
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.kakao.sdk.common.KakaoSdk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okio.Path.Companion.toPath
import org.bmsk.lifemash.error.CrashlyticsErrorReporter
import org.bmsk.lifemash.fcm.PushNotificationService
import org.bmsk.lifemash.feature.shared.error.ErrorReporter
import org.bmsk.lifemash.main.di.appKoinModules
import org.bmsk.lifemash.notification.data.db.getNotificationKeywordDBBuilder
import org.bmsk.lifemash.notification.data.di.notificationDataModule
import org.bmsk.lifemash.notification.domain.usecase.SyncKeywordsUseCase
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module
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
                appKoinModules(
                    backendBaseUrl = BuildConfig.BACKEND_BASE_URL,
                    dataStore = dataStore,
                    notificationDataModule = notificationDataModule(
                        getNotificationKeywordDBBuilder(this@LifeMashApplication),
                    ),
                    platformModules = listOf(
                        module { single { PushNotificationService() } },
                        module { single<ErrorReporter> { CrashlyticsErrorReporter() } },
                    ),
                ),
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
