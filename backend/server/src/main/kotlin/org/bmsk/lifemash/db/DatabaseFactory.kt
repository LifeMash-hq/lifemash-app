package org.bmsk.lifemash.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.bmsk.lifemash.db.tables.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.util.UUID

/**
 * 데이터베이스 연결 및 테이블 초기화를 담당하는 팩토리.
 *
 * 사용하는 기술:
 * - PostgreSQL: 관계형 데이터베이스 (데이터를 테이블/행/열로 저장)
 * - HikariCP: DB 커넥션 풀 — 매번 연결을 새로 만들지 않고 미리 연결을 만들어 재사용 (성능 최적화)
 * - Exposed: Kotlin용 ORM — SQL을 직접 쓰지 않고 Kotlin 코드로 DB를 조작
 */
object DatabaseFactory {
    fun init() {
        // 환경변수에서 DB 접속 URL을 가져옴 (예: jdbc:postgresql://host:5432/dbname?user=...&password=...)
        val databaseUrl = org.bmsk.lifemash.config.EnvConfig.require("DATABASE_URL")

        // 커넥션 풀 설정
        val config = HikariConfig().apply {
            jdbcUrl = databaseUrl
            driverClassName = "org.postgresql.Driver"
            maximumPoolSize = 5  // 동시에 유지할 최대 DB 연결 수
            isAutoCommit = false // 트랜잭션을 명시적으로 커밋해야 함 (데이터 일관성 보장)
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"  // 트랜잭션 격리 수준: 같은 트랜잭션 내에서 반복 읽기 시 동일 결과 보장
            validate()
        }

        Database.connect(HikariDataSource(config))

        // 서버 시작 시 테이블이 없으면 자동 생성 (이미 있으면 무시)
        transaction {
            SchemaUtils.create(
                Users, Groups, GroupMembers, Events, Comments,
                AssistantConversations, AssistantMessages, AssistantUsage, UserApiKeys,
                MarketplaceBlocks,
            )
        }

        seedSystemBlocks()
    }

    /** 시스템 제공 블록(뉴스 등)을 마켓플레이스에 등록. 이미 존재하면 무시하고, url은 항상 최신으로 유지. */
    private fun seedSystemBlocks() {
        val newsBlockId = UUID.fromString("00000000-0000-0000-0000-000000000001")
        val newsBlockUrl = "https://lifemash-e79c5.web.app/blocks/news/"

        transaction {
            MarketplaceBlocks.insertIgnore {
                it[id] = newsBlockId
                it[name] = "LifeMash 뉴스"
                it[url] = newsBlockUrl
                it[description] = "키워드 기반 뉴스 기사 검색"
                it[creatorId] = null
                it[status] = "APPROVED"
                it[createdAt] = System.currentTimeMillis()
                it[toolDefinitions] = """[{
                    "name": "search_news",
                    "description": "뉴스 기사를 키워드로 검색합니다. 특정 주제나 키워드와 관련된 최신 뉴스를 가져올 때 사용합니다.",
                    "input_schema": {
                        "type": "object",
                        "properties": {
                            "keyword": {"type": "string", "description": "검색 키워드 (예: '삼성전자', 'AI', '경제')"},
                            "limit": {"type": "integer", "description": "가져올 기사 수 (기본값: 10, 최대: 100)"},
                            "category": {"type": "string", "description": "카테고리 필터 (politics, economy, tech 등). 생략 시 전체."}
                        },
                        "required": ["keyword"]
                    },
                    "executionUrl": "https://asia-northeast3-lifemash-e79c5.cloudfunctions.net/newsBlockToolHandler"
                }]""".trimIndent()
            }
            // 시스템 블록의 url은 항상 최신 값으로 유지 (insertIgnore는 기존 레코드 미갱신)
            MarketplaceBlocks.update({ MarketplaceBlocks.id eq newsBlockId }) {
                it[url] = newsBlockUrl
            }
        }
    }
}
