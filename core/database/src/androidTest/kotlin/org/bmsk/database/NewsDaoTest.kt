package org.bmsk.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.bmsk.database.dao.NewsDao
import org.bmsk.database.model.entity.NewsEntity
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Date
import junit.framework.TestCase.assertEquals
import org.mockito.Mock

@RunWith(AndroidJUnit4::class)
class NewsDaoTest {

    @Mock
    private lateinit var newsDao: NewsDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()
        newsDao = db.newsDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun testInsertAndDeleteNews() = runBlocking {
        val newsEntity = createNewsEntity()

        newsDao.insertNews(newsEntity)

        val newsList = newsDao.selectAllNews().first()
        assertEquals(newsList[0], newsEntity)

        newsDao.deleteNews(newsEntity)

        val emptyNewsList = newsDao.selectAllNews().first()
        assert(emptyNewsList.isEmpty())
    }

    private fun createNewsEntity() = NewsEntity(
        title = "[Hot] Today's Android Developer: BMSK",
        link = "https://www.test.com",
        pubDate = "2023-06-24 11:11:00",
        imageUrl = null,
        createdDate = Date()
    )
}