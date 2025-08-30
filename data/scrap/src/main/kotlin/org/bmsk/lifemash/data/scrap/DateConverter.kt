package org.bmsk.lifemash.data.scrap

import androidx.room.TypeConverter
import java.util.Date

internal class DateConverter {
    @TypeConverter
    fun fromTimestamp(value: Long): Date = Date(value)

    @TypeConverter
    fun dateToTimestamp(date: Date): Long = date.time
}
