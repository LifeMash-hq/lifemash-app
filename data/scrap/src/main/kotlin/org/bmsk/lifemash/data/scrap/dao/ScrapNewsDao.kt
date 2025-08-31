package org.bmsk.lifemash.data.scrap.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import org.bmsk.lifemash.data.scrap.entity.NewsEntity
import java.util.Date

@Dao
interface ScrapNewsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNews(news: NewsEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllNews(newsList: List<NewsEntity>)

    @Query("SELECT * FROM scrap_news")
    fun getAllNews(): Flow<List<NewsEntity>>

    @Query("DELETE FROM scrap_news WHERE link = :link")
    fun deleteNewsByLink(link: String)

    @Delete
    fun deleteNews(news: NewsEntity)

    @Query("DELETE FROM scrap_news")
    fun deleteAllNews()

    @Update
    fun updateNews(news: NewsEntity)

    @Update
    fun updateAllNews(newsList: List<NewsEntity>)

    @Query("SELECT * FROM scrap_news WHERE title = :title LIMIT 1")
    fun getNewsByTitle(title: String): NewsEntity?

    @Query("SELECT * FROM scrap_news WHERE link = :link LIMIT 1")
    fun getNewsByLink(link: String): NewsEntity?

    @Query("SELECT * FROM scrap_news WHERE pub_date = :pubDate LIMIT 1")
    fun getNewsByPubDate(pubDate: Date): NewsEntity?

    @Query("SELECT * FROM scrap_news WHERE image_url = :imageUrl LIMIT 1")
    fun getNewsByImageUrl(imageUrl: String): NewsEntity?
}