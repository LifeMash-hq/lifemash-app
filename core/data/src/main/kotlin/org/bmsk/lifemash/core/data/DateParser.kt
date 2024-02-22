package org.bmsk.lifemash.core.data

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object DateParser {

    fun parseDate(input: String): String {
        val parser = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH)
        val date = parser.parse(input) as Date

        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREAN).apply {
            timeZone = TimeZone.getTimeZone("Asia/Seoul")
        }

        return formatter.format(date)
    }
}
