package org.bmsk.lifemash.config

/**
 * 환경 설정 조회: 환경변수 → 시스템 프로퍼티 → 기본값 순으로 탐색.
 * .env 파일은 Application.kt에서 시스템 프로퍼티로 로드된다.
 */
object EnvConfig {
    fun get(key: String): String? =
        System.getenv(key) ?: System.getProperty(key)

    fun require(key: String): String =
        get(key) ?: error("$key environment variable is required")
}
