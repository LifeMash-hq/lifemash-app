package org.bmsk.lifemash.comment

import org.bmsk.lifemash.model.calendar.CommentDto
import org.bmsk.lifemash.model.calendar.CreateCommentRequest
import kotlin.time.Clock
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertTrue

class CommentDtoTest {

    @Test
    fun `CommentDto가 정상 직렬화된다`() {
        val dto = CommentDto(
            id = "comment-1",
            eventId = "event-1",
            authorId = "user-1",
            content = "테스트 댓글",
            createdAt = Clock.System.now(),
        )

        val jsonStr = Json.encodeToString(dto)
        assertContains(jsonStr, "테스트 댓글")
    }

    @Test
    fun `CreateCommentRequest 빈 내용 검증`() {
        val request = CreateCommentRequest(content = "댓글 내용")
        assertTrue(request.content.isNotBlank())
    }
}
