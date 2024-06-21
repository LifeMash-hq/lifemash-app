package org.bmsk.lifemash.core.repo.scrap.impl.db

import androidx.room.Database
import org.bmsk.lifemash.core.repo.scrap.impl.dao.ScrapNewsDao
import org.bmsk.lifemash.core.repo.scrap.impl.entity.NewsEntity

@Database(entities = [NewsEntity::class], version = 1)
abstract class ScrapNewsDB {
    abstract fun newsDao(): ScrapNewsDao
}
