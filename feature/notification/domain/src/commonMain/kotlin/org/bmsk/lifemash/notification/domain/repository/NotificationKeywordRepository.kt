package org.bmsk.lifemash.notification.domain.repository

import kotlinx.coroutines.flow.Flow
import org.bmsk.lifemash.notification.domain.model.Keyword
import org.bmsk.lifemash.notification.domain.model.NotificationKeyword

interface KeywordRepository {
    fun getKeywords(): Flow<List<NotificationKeyword>>
    suspend fun addKeyword(keyword: Keyword)
    suspend fun removeKeyword(id: Long)
}

interface KeywordSyncRepository {
    suspend fun syncKeywords(deviceToken: String)
}
