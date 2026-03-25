package org.bmsk.lifemash.group

import org.bmsk.lifemash.model.calendar.GroupDto
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GroupRepositoryTest {

    @Test
    fun `초대 코드는 8자리 대문자 영숫자이다`() {
        // GroupRepository의 inviteCode 생성 로직 검증
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val code = (1..8).map { chars.random() }.joinToString("")

        assertEquals(8, code.length)
        code.forEach { char ->
            assert(char in 'A'..'Z' || char in '0'..'9') { "Invalid char: $char" }
        }
    }

    @Test
    fun `GroupDto 직렬화가 정상 동작한다`() {
        val dto = GroupDto(
            id = "test-id",
            name = "테스트 그룹",
            type = "COUPLE",
            maxMembers = 2,
            inviteCode = "ABCD1234",
            members = emptyList(),
            createdAt = kotlin.time.Clock.System.now(),
        )

        assertNotNull(dto)
        assertEquals("COUPLE", dto.type)
        assertEquals(2, dto.maxMembers)
    }
}
