package org.bmsk.lifemash.fcm

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.messaging.messaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

actual class PushNotificationService {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    actual fun registerToken(onToken: (String) -> Unit) {
        scope.launch {
            runCatching { Firebase.messaging.getToken() }
                .onSuccess { onToken(it) }
        }
    }

    actual fun unregisterToken() {
        scope.launch {
            runCatching { Firebase.messaging.deleteToken() }
        }
    }
}
