package org.bmsk.lifemash.main

import androidx.compose.ui.window.ComposeUIViewController
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import okio.Path.Companion.toPath
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.initialize
import org.bmsk.lifemash.feature.designsystem.theme.LifeMashTheme
import org.bmsk.lifemash.main.di.appKoinModules
import org.bmsk.lifemash.notification.data.db.getNotificationKeywordDBBuilder
import org.bmsk.lifemash.notification.data.di.notificationDataModule
import org.koin.core.context.startKoin
import platform.Foundation.NSHomeDirectory

private const val BACKEND_BASE_URL = "https://lifemash-backend.onrender.com"

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
                notificationDataModule = notificationDataModule(getNotificationKeywordDBBuilder()),
            ),
        )
    }
}

fun MainViewController() = ComposeUIViewController {
    LifeMashTheme {
        MainScreen()
    }
}
