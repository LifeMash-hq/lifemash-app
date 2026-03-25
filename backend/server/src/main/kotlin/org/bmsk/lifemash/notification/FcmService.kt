package org.bmsk.lifemash.notification

import java.util.*

interface FcmService {
    fun notifyGroupExcept(groupId: UUID, excludeUserId: UUID, title: String, body: String)
}
