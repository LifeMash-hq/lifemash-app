package org.bmsk.lifemash.fcm

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bmsk.lifemash.notification.domain.usecase.SyncKeywordsUseCase
import org.koin.android.ext.android.inject

class LifeMashFcmService : FirebaseMessagingService() {

    private val syncKeywordsUseCase: SyncKeywordsUseCase by inject()
    private val displayer by lazy { NotificationDisplayer(this) }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                syncKeywordsUseCase(token)
            } catch (_: Exception) {
                // 토큰 동기화 실패 시 다음 기회에 재시도
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
