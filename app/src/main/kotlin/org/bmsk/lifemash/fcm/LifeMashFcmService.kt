package org.bmsk.lifemash.fcm

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bmsk.lifemash.feature.shared.error.ErrorReporter
import org.bmsk.lifemash.notification.domain.usecase.SyncKeywordsUseCase
import org.koin.android.ext.android.inject
import java.io.IOException

class LifeMashFcmService : FirebaseMessagingService() {

    private val syncKeywordsUseCase: SyncKeywordsUseCase by inject()
    private val errorReporter: ErrorReporter by inject()
    private val displayer by lazy { NotificationDisplayer(this, errorReporter) }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                syncKeywordsUseCase(token)
            } catch (e: IOException) {
                errorReporter.log("FCM token sync: network unavailable")
            } catch (e: Exception) {
                errorReporter.report(e)
            }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val title = message.notification?.title ?: "LifeMash"
        val body = message.notification?.body ?: return
        val articleUrl = message.data["url"]
        displayer.show(title, body, articleUrl)
    }
}
