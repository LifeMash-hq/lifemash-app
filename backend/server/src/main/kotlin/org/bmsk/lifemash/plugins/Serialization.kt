package org.bmsk.lifemash.plugins

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json

/**
 * JSON 직렬화/역직렬화 설정.
 *
 * ContentNegotiation 플러그인:
 *   클라이언트가 보낸 JSON 요청 본문 → Kotlin 객체로 변환 (역직렬화)
 *   Kotlin 객체 → JSON 응답 본문으로 변환 (직렬화)
 *
 * 옵션 설명:
 * - ignoreUnknownKeys: 클라이언트가 서버에 없는 필드를 보내도 에러 없이 무시
 * - isLenient: JSON 형식이 약간 느슨해도(따옴표 누락 등) 허용
 * - encodeDefaults: 기본값이 있는 필드도 응답 JSON에 포함
 * - prettyPrint: false → JSON을 한 줄로 압축 (네트워크 트래픽 절약)
 */
fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            isLenient = true
            encodeDefaults = true
            prettyPrint = false
        })
    }
}
