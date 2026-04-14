package org.bmsk.lifemash.fcm

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.bmsk.lifemash.domain.error.ErrorReporter
import org.koin.android.ext.android.inject

class LifeMashFcmService : FirebaseMessagingService() {

    private val errorReporter: ErrorReporter by inject()
    private val displayer by lazy { NotificationDisplayer(this, errorReporter) }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val title = message.notification?.title ?: "LifeMash"
        val body = message.notification?.body ?: return
        val articleUrl = message.data["url"]
        displayer.show(
            title,
            body,
            articleUrl,
        )
    }
}
