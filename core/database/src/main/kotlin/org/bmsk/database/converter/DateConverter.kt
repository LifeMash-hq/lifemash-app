package org.bmsk.database.converter

import androidx.room.TypeConverter
import org.bmsk.database.util.DateUtil
import java.util.Date

class DateConverter {
    @TypeConverter
    fun toDate(timeStamp: String?): Date? {
        return timeStamp?.let { DateUtil.dbDateFormat.parse(it) }
    }

    @TypeConverter
    fun toTimeStamp(date: Date?): String? {
        return date?.let { DateUtil.dbDateFormat.format(it) }
    }
}