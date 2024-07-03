package org.bmsk.lifemash.core.model

import org.junit.Assert.assertEquals
import org.junit.Test

class DateParserTest {

    @Test
    @Throws(Exception::class)
    fun testDateFormat() {
        // 서버로부터 받은 GMT 시간 문자열
        val input = "Tue, 20 Jun 2023 02:57:43 GMT"

        val output = DateParser.formatDate(DateParser.parseDate(input))

        // 기대하는 결과값
        val expected = "2023-06-20 11:57:43"
        assertEquals(expected, output)
    }
}
