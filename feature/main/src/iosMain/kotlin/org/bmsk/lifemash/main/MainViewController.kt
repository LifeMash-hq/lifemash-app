package org.bmsk.lifemash.main

import androidx.compose.ui.window.ComposeUIViewController
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import okio.Path.Companion.toPath
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.initialize
import org.bmsk.lifemash.designsystem.theme.LifeMashTheme
import org.bmsk.lifemash.main.di.appKoinModules
import org.koin.core.context.startKoin
import platform.Foundation.NSHomeDirectory

// 로컬 개발: local.properties에 BACKEND_BASE_URL 설정 시 해당 URL 사용 (기본: http://localhost:8080)
// 프로덕션: local.properties 미설정 시 빌드 서버의 생성 파일에 의해 프로덕션 URL로 대체됨
private const val BACKEND_BASE_URL = IOS_DEBUG_BACKEND_URL

private val iosDataStore = PreferenceDataStoreFactory.createWithPath {
    (NSHomeDirectory() + "/Documents/lifemash_prefs.preferences_pb").toPath()
}

fun initKoin() {
    Firebase.initialize()
    startKoin {
        modules(
            appKoinModules(
                backendBaseUrl = BACKEND_BASE_URL,
                dataStore = iosDataStore,
            ),
        )
    }
}

fun MainViewController() = ComposeUIViewController {
    LifeMashTheme {
        MainScreen()
    }
}
