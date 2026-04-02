package org.bmsk.lifemash.fake

import org.bmsk.lifemash.notification.FcmService
import kotlin.uuid.Uuid

class FakeFcmService : FcmService {
    val notifications = mutableListOf<NotificationRecord>()

    data class NotificationRecord(
        val groupId: Uuid,
        val excludeUserId: Uuid,
        val title: String,
        val body: String
    )

    override fun notifyGroupExcept(groupId: Uuid, excludeUserId: Uuid, title: String, body: String) {
        notifications.add(NotificationRecord(groupId, excludeUserId, title, body))
    }
}
