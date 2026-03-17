package org.bmsk.lifemash.notification.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.bmsk.lifemash.notification.data.db.NotificationKeywordDao
import org.bmsk.lifemash.notification.data.db.NotificationKeywordEntity
import org.bmsk.lifemash.notification.data.source.FcmTokenFirestoreSource
import org.bmsk.lifemash.notification.domain.model.Keyword
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NotificationKeywordRepositoryImplTest {

    private var nextId = 1L
    private val entities = mutableListOf<NotificationKeywordEntity>()
    private val entitiesFlow = MutableStateFlow<List<NotificationKeywordEntity>>(emptyList())

    private var syncedToken: String? = null
    private var syncedKeywords: List<String>? = null

    private val fakeDao = object : NotificationKeywordDao {
        override fun getAll(): Flow<List<NotificationKeywordEntity>> = entitiesFlow
        override suspend fun getAllOnce(): List<NotificationKeywordEntity> = entities.toList()
        override suspend fun insert(entity: NotificationKeywordEntity) {
            if (entities.any { it.keyword == entity.keyword }) return // UNIQUE 시뮬레이션
            entities.add(entity.copy(id = nextId++))
            entitiesFlow.value = entities.toList()
        }
        override suspend fun delete(id: Long) {
            entities.removeAll { it.id == id }
            entitiesFlow.value = entities.toList()
        }
    }

    private val fakeFirestoreSource = object : FcmTokenFirestoreSource {
        override suspend fun syncKeywords(fcmToken: String, keywords: List<String>) {
            syncedToken = fcmToken
            syncedKeywords = keywords
        }
        override suspend fun updateUserId(fcmToken: String, userId: String) {}
    }

    private lateinit var repository: NotificationKeywordRepositoryImpl

    @BeforeTest
    fun setUp() {
        nextId = 1L
        entities.clear()
        entitiesFlow.value = emptyList()
        syncedToken = null
        syncedKeywords = null
        repository = NotificationKeywordRepositoryImpl(fakeDao, fakeFirestoreSource)
    }

    @Test
    fun `키워드 추가 후 getKeywords에 반영된다`() = runTest {
        repository.addKeyword(Keyword("삼성 반도체"))

        val keywords = repository.getKeywords().first()
        assertEquals(1, keywords.size)
        assertEquals("삼성 반도체", keywords[0].keyword.value)
    }

    @Test
    fun `중복 키워드는 무시된다`() = runTest {
        repository.addKeyword(Keyword("삼성"))
        repository.addKeyword(Keyword("삼성"))

        val keywords = repository.getKeywords().first()
        assertEquals(1, keywords.size)
    }

    @Test
    fun `키워드 삭제 후 getKeywords에서 제거된다`() = runTest {
        repository.addKeyword(Keyword("삼성"))
        repository.addKeyword(Keyword("ai"))
        val keywords = repository.getKeywords().first()
        val idToDelete = keywords.first { it.keyword.value == "삼성" }.id

        repository.removeKeyword(idToDelete)

        val result = repository.getKeywords().first()
        assertEquals(1, result.size)
        assertEquals("ai", result[0].keyword.value)
    }

    @Test
    fun `syncKeywords가 현재 키워드 목록을 전달한다`() = runTest {
        repository.addKeyword(Keyword("삼성"))
        repository.addKeyword(Keyword("반도체"))

        repository.syncKeywords("fake-token-123")

        assertEquals("fake-token-123", syncedToken)
        assertEquals(listOf("삼성", "반도체"), syncedKeywords)
    }

    @Test
    fun `키워드가 없을 때 syncKeywords는 빈 배열을 전달한다`() = runTest {
        repository.syncKeywords("fake-token")

        assertEquals("fake-token", syncedToken)
        assertTrue(syncedKeywords!!.isEmpty())
    }
}
