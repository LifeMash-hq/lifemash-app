package org.bmsk.database.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.bmsk.database.model.entity.NewsEntity

interface NewsDao {
    @Query("SELECT * FROM News ORDER BY createdDate DESC")
    fun selectAllNews(): Flow<List<NewsEntity>>

    @Delete
    suspend fun deleteNews(item: NewsEntity)

    @Insert(onConflict = REPLACE)
    suspend fun insertNews(item: NewsEntity)
}