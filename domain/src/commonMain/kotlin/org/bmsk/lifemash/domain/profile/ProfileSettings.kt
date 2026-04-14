package org.bmsk.lifemash.domain.profile

data class ProfileSettings(
    val defaultSubTab: String = "moments",
    val myCalendarViewMode: String = "dot",
    val othersCalendarViewMode: String = "dot",
    val defaultEventVisibility: String = "public",
)
