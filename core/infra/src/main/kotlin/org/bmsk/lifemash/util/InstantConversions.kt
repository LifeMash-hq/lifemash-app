package org.bmsk.lifemash.util

import kotlin.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

/** kotlin.time.Instant → java.time.OffsetDateTime (UTC) */
fun Instant.toOffsetDateTime(): OffsetDateTime =
    OffsetDateTime.ofInstant(
        java.time.Instant.ofEpochSecond(epochSeconds, nanosecondsOfSecond.toLong()),
        ZoneOffset.UTC,
    )

/** java.time.OffsetDateTime → kotlin.time.Instant */
fun OffsetDateTime.toKotlinxInstant(): Instant {
    val javaInstant = toInstant()
    return Instant.fromEpochSeconds(javaInstant.epochSecond, javaInstant.nano)
}

/** 현재 시각을 java.time.OffsetDateTime (UTC)로 반환 */
fun nowUtc(): OffsetDateTime = OffsetDateTime.now(ZoneOffset.UTC)
