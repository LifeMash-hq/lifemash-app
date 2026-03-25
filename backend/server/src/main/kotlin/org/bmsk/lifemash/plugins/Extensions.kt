package org.bmsk.lifemash.plugins

import java.util.*

/** String을 UUID로 변환. 형식이 잘못된 경우 IllegalArgumentException → StatusPages에서 400으로 처리됨 */
fun String.toUUID(): UUID = UUID.fromString(this)
