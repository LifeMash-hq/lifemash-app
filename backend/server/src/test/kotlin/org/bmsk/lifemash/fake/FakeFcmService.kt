package org.bmsk.lifemash.fake

import org.bmsk.lifemash.notification.FcmService
import java.util.*

class FakeFcmService : FcmService {
    data class NotificationRecord(val groupId: UUID, val excludeUserId: UUID, val title: String, val body: String)

    val notifications = mutableListOf<NotificationRecord>()

    override fun notifyGroupExcept(groupId: UUID, excludeUserId: UUID, title: String, body: String) {
        notifications.add(NotificationRecord(groupId, excludeUserId, title, body))
    }
}
