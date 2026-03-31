package org.bmsk.lifemash.validation

object GroupLimits {
    const val MAX_NAME_LENGTH = GroupName.MAX_LENGTH

    fun maxMembers(type: String): Int = when (type) {
        "COUPLE" -> 2
        else -> 50
    }
}
