package org.bmsk.lifemash.config

/**
 * 환경변수 접근 유틸리티.
 */
object EnvConfig {
    fun get(key: String): String? =
        System.getenv(key) ?: System.getProperty(key)

    fun require(key: String): String =
        get(key) ?: error("$key environment variable is required")
}
