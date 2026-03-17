package org.bmsk.lifemash.notification.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Clock
import kotlin.time.Instant
import org.bmsk.lifemash.notification.data.db.NotificationKeywordDao
import org.bmsk.lifemash.notification.data.db.NotificationKeywordEntity
import org.bmsk.lifemash.notification.data.source.FcmTokenFirestoreSource
import org.bmsk.lifemash.notification.domain.model.Keyword
import org.bmsk.lifemash.notification.domain.model.NotificationKeyword
import org.bmsk.lifemash.notification.domain.repository.KeywordRepository
import org.bmsk.lifemash.notification.domain.repository.KeywordSyncRepository

internal class NotificationKeywordRepositoryImpl(
    private val dao: NotificationKeywordDao,
    private val firestoreSource: FcmTokenFirestoreSource,
) : KeywordRepository, KeywordSyncRepository {

    override fun getKeywords(): Flow<List<NotificationKeyword>> =
        dao.getAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun addKeyword(keyword: Keyword) {
        dao.insert(
            NotificationKeywordEntity(
                keyword = keyword.value,
                createdAt = Clock.System.now().toEpochMilliseconds(),
            )
        )
    }

    override suspend fun removeKeyword(id: Long) {
        dao.delete(id)
    }

    override suspend fun syncKeywords(deviceToken: String) {
        val keywords = dao.getAllOnce().map { it.keyword }
        firestoreSource.syncKeywords(deviceToken, keywords)
    }

    private fun NotificationKeywordEntity.toDomain() = NotificationKeyword(
        id = id,
        keyword = Keyword(keyword),
        createdAt = Instant.fromEpochMilliseconds(createdAt),
    )
}
