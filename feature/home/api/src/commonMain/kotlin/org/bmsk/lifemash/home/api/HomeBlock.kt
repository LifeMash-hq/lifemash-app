package org.bmsk.lifemash.home.api

sealed class HomeBlock(val id: String) {
    abstract val visible: Boolean

    data class CalendarToday(override val visible: Boolean = true) : HomeBlock("CALENDAR_TODAY")
    data class Groups(override val visible: Boolean = true) : HomeBlock("GROUPS")
    data class Assistant(override val visible: Boolean = true) : HomeBlock("ASSISTANT")
    data class WebViewBlock(
        val blockId: String,
        val url: String,
        override val visible: Boolean = true,
    ) : HomeBlock(blockId)
}
