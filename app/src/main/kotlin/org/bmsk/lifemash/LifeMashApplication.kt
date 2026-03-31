package org.bmsk.lifemash

import android.app.Application
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.kakao.sdk.common.KakaoSdk
import okio.Path.Companion.toPath
import org.bmsk.lifemash.error.CrashlyticsErrorReporter
import org.bmsk.lifemash.fcm.PushNotificationService
import org.bmsk.lifemash.feature.shared.error.ErrorReporter
import org.bmsk.lifemash.main.di.appKoinModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

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
                    platformModules = listOf(
                        module { single { PushNotificationService() } },
                        module { single<ErrorReporter> { CrashlyticsErrorReporter() } },
                    ),
                ),
            )
        }
    }
}
