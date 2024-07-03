package org.bmsk.lifemash.core.repo.scrap.impl.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import java.util.Date

@Entity(tableName = "scrap_news")
data class NewsEntity(

    @ColumnInfo(name = "title")
    val title: String,

    @PrimaryKey
    @ColumnInfo(name = "link")
    val link: String,

    @ColumnInfo(name = "pub_date")
    val pubDate: Date,

    @ColumnInfo(name = "image_url")
    val imageUrl: String?,
)

internal val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // 테이블을 새 이름으로 변경
        db.execSQL("ALTER TABLE scrap_news RENAME TO scrap_news_old")

        // 새로운 테이블 생성
        db.execSQL("""
            CREATE TABLE scrap_news (
                title TEXT NOT NULL,
                link TEXT NOT NULL PRIMARY KEY,
                pub_date INTEGER NOT NULL,
                image_url TEXT
            )
        """)

        // 중복된 link 값을 제거하며 데이터를 옮기기 위해 임시 테이블 생성
        db.execSQL("""
            CREATE TEMPORARY TABLE scrap_news_temp AS 
            SELECT * FROM scrap_news_old 
            GROUP BY link
        """)

        // 임시 테이블에서 새로운 테이블로 데이터 삽입
        db.execSQL("""
            INSERT INTO scrap_news (title, link, pub_date, image_url)
            SELECT title, link, pub_date, image_url
            FROM scrap_news_temp
        """)

        // 임시 테이블 삭제
        db.execSQL("DROP TABLE scrap_news_temp")

        // 기존 테이블 삭제
        db.execSQL("DROP TABLE scrap_news_old")
    }
}