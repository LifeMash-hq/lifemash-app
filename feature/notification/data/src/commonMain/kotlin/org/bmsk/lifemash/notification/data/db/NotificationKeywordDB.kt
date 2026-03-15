package org.bmsk.lifemash.notification.data.db

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor

@Database(entities = [NotificationKeywordEntity::class], version = 1, exportSchema = false)
@ConstructedBy(NotificationKeywordDBConstructor::class)
abstract class NotificationKeywordDB : RoomDatabase() {
    abstract fun keywordDao(): NotificationKeywordDao
}

// Room KMP: expect/actual 패턴으로 플랫폼별 DB 인스턴스 생성
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object NotificationKeywordDBConstructor : RoomDatabaseConstructor<NotificationKeywordDB>
