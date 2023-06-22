package org.bmsk.database.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity("News")
data class NewsEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    val title: String,
    @ColumnInfo
    val link: String,
    @ColumnInfo
    val pubDate: String,
    @ColumnInfo
    val imageUrl: String? = null,
    @ColumnInfo
    val createdDate: Date
)
