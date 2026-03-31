package org.bmsk.lifemash.notification

import kotlin.uuid.Uuid

interface FcmService {
    fun notifyGroupExcept(groupId: Uuid, excludeUserId: Uuid, title: String, body: String)
}
