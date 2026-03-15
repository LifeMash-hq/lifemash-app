package org.bmsk.lifemash.notification.domain.repository

import kotlinx.coroutines.flow.Flow
import org.bmsk.lifemash.notification.domain.model.NotificationKeyword

interface NotificationKeywordRepository {
    fun getKeywords(): Flow<List<NotificationKeyword>>
    suspend fun addKeyword(keyword: String)
    suspend fun removeKeyword(id: Long)
    suspend fun syncToFirestore(fcmToken: String)
}
