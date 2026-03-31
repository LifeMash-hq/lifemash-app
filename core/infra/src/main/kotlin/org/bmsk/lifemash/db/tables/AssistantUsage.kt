package org.bmsk.lifemash.db.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.date

/**
 * assistant_usage 테이블 — 사용자별 일일 AI 사용량 추적.
 * 자체 API 키가 없는 사용자에게 일일 요청 제한(20회)을 적용하기 위해 사용.
 * (userId + date) 복합 유니크: 사용자당 하루에 하나의 레코드만 존재.
 */
object AssistantUsage : Table("assistant_usage") {
    val id = uuid("id").autoGenerate()
    val userId = uuid("user_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
    val date = date("date")                                     // 사용일 (날짜만, 시각 없음)
    val inputTokens = integer("input_tokens").default(0)        // Claude API에 보낸 토큰 수
    val outputTokens = integer("output_tokens").default(0)      // Claude API가 생성한 토큰 수
    val requestCount = integer("request_count").default(0)      // 해당 날짜의 요청 횟수

    override val primaryKey = PrimaryKey(id)

    init {
        uniqueIndex(userId, date)
    }
}
