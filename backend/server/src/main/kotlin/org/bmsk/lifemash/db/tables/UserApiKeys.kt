package org.bmsk.lifemash.db.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestampWithTimeZone

/**
 * user_api_keys 테이블 — 사용자가 직접 등록한 Claude API 키를 암호화하여 저장.
 * 자체 키를 등록하면 일일 사용 제한 없이 AI 어시스턴트를 사용할 수 있다.
 * userId에 uniqueIndex → 사용자당 하나의 API 키만 저장.
 */
object UserApiKeys : Table("user_api_keys") {
    val id = uuid("id").autoGenerate()
    val userId = uuid("user_id").references(Users.id, onDelete = ReferenceOption.CASCADE).uniqueIndex()
    val encryptedKey = text("encrypted_key")            // AES-GCM으로 암호화된 API 키
    val provider = varchar("provider", 20)               // AI 서비스 제공자 (현재 "claude")
    val createdAt = timestampWithTimeZone("created_at")
    val updatedAt = timestampWithTimeZone("updated_at")

    override val primaryKey = PrimaryKey(id)
}
