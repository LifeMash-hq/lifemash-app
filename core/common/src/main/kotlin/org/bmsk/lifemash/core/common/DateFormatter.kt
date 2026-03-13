package org.bmsk.lifemash.core.common

import java.time.ZoneId
import java.time.format.DateTimeFormatter

val ArticleDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    .withZone(ZoneId.systemDefault())
