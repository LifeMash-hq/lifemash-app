package org.bmsk.lifemash.db.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestampWithTimeZone

/**
 * assistant_messages 테이블 — AI 대화의 개별 메시지.
 * 대화(Conversation) 삭제 시 관련 메시지도 함께 삭제 (CASCADE).
 */
object AssistantMessages : Table("assistant_messages") {
    val id = uuid("id").autoGenerate()
    val conversationId = uuid("conversation_id").references(AssistantConversations.id, onDelete = ReferenceOption.CASCADE)
    val role = varchar("role", 20)                              // "user" 또는 "assistant"
    val content = text("content")                                // 메시지 내용
    val toolCallsJson = text("tool_calls_json").nullable()      // AI가 사용한 도구 호출 기록 (JSON, 선택)
    val createdAt = timestampWithTimeZone("created_at")

    override val primaryKey = PrimaryKey(id)

    init {
        index(false, conversationId)
    }
}
