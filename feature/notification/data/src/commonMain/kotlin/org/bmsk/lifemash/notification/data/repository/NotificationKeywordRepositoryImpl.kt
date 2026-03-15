package org.bmsk.lifemash.notification.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.bmsk.lifemash.notification.data.db.NotificationKeywordDao
import org.bmsk.lifemash.notification.data.db.NotificationKeywordEntity
import org.bmsk.lifemash.notification.data.source.FcmTokenFirestoreSource
import org.bmsk.lifemash.notification.domain.model.NotificationKeyword
import org.bmsk.lifemash.notification.domain.repository.NotificationKeywordRepository

internal class NotificationKeywordRepositoryImpl(
    private val dao: NotificationKeywordDao,
    private val firestoreSource: FcmTokenFirestoreSource,
) : NotificationKeywordRepository {

    override fun getKeywords(): Flow<List<NotificationKeyword>> =
        dao.getAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun addKeyword(keyword: String) {
        dao.insert(
            NotificationKeywordEntity(
                keyword = keyword.trim().lowercase(),
                createdAt = Clock.System.now().toEpochMilliseconds(),
            )
        )
    }

    override suspend fun removeKeyword(id: Long) {
        dao.delete(id)
    }

    override suspend fun syncToFirestore(fcmToken: String) {
        val keywords = dao.getAllOnce().map { it.keyword }
        firestoreSource.syncKeywords(fcmToken, keywords)
    }

    private fun NotificationKeywordEntity.toDomain() = NotificationKeyword(
        id = id,
        keyword = keyword,
        createdAt = Instant.fromEpochMilliseconds(createdAt),
    )
}
