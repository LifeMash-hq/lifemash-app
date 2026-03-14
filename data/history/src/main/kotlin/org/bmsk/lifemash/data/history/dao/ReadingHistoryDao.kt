package org.bmsk.lifemash.data.history.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.bmsk.lifemash.data.history.entity.ReadingRecordEntity

@Dao
interface ReadingHistoryDao {
    @Query("SELECT articleId FROM reading_history")
    fun getAllReadArticleIds(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: ReadingRecordEntity)
}
