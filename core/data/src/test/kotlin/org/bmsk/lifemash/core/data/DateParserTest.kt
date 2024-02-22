package org.bmsk.lifemash.core.data

import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.mockito.Mock

class DateParserTest {
    @Mock
    val dateParser = DateParser

    @Test
    @Throws(Exception::class)
    fun testDateFormat() {
        // 서버로부터 받은 GMT 시간 문자열
        val input = "Tue, 20 Jun 2023 02:57:43 GMT"

        val output = dateParser.parseDate(input)

        // 기대하는 결과값
        val expected = "2023-06-20 11:57:43"
        assertEquals(expected, output)
    }
}
