package org.bmsk.lifemash.db.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestampWithTimeZone

/**
 * assistant_conversations 테이블 — AI 어시스턴트와의 대화 세션.
 * 사용자별로 여러 대화를 가질 수 있으며, 각 대화에는 여러 메시지가 포함된다.
 */
object AssistantConversations : Table("assistant_conversations") {
    val id = uuid("id").autoGenerate()
    val userId = uuid("user_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
    val title = varchar("title", 200)  // 대화 제목 (첫 메시지 앞 50자로 자동 설정)
    val createdAt = timestampWithTimeZone("created_at")
    val updatedAt = timestampWithTimeZone("updated_at")  // 마지막 메시지 시각 (대화 목록 정렬용)

    override val primaryKey = PrimaryKey(id)

    init {
        index(false, userId)
    }
}
