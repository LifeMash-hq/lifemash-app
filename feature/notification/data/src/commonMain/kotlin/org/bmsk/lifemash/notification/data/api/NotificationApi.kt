package org.bmsk.lifemash.notification.data.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import org.bmsk.lifemash.model.notification.NotificationDto
import org.bmsk.lifemash.model.notification.UnreadCountResponse

internal class NotificationApi(private val client: HttpClient) {

    suspend fun getNotifications(): List<NotificationDto> =
        client.get("/api/v1/notifications").body()

    suspend fun getUnreadCount(): Int =
        client.get("/api/v1/notifications/unread-count").body<UnreadCountResponse>().count

    suspend fun markAsRead(notificationId: String) {
        client.post("/api/v1/notifications/$notificationId/read")
    }
}
