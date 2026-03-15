package org.bmsk.lifemash.notification.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationKeywordDao {
    @Query("SELECT * FROM notification_keywords ORDER BY createdAt DESC")
    fun getAll(): Flow<List<NotificationKeywordEntity>>

    @Query("SELECT * FROM notification_keywords ORDER BY createdAt DESC")
    suspend fun getAllOnce(): List<NotificationKeywordEntity>

    @Insert
    suspend fun insert(entity: NotificationKeywordEntity)

    @Query("DELETE FROM notification_keywords WHERE id = :id")
    suspend fun delete(id: Long)
}
