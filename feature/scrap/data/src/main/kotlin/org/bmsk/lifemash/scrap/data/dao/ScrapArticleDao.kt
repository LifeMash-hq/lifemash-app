package org.bmsk.lifemash.scrap.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.bmsk.lifemash.scrap.data.entity.ArticleEntity

@Dao
interface ScrapArticleDao {
    @Query("SELECT * FROM scrap_articles ORDER BY publishedAt DESC")
    fun getAllArticles(): Flow<List<ArticleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticle(article: ArticleEntity)

    @Query("DELETE FROM scrap_articles WHERE id = :articleId")
    suspend fun deleteArticle(articleId: String)
}
