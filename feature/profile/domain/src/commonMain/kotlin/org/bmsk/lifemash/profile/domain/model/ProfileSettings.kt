package org.bmsk.lifemash.profile.domain.model

data class ProfileSettings(
    val defaultSubTab: String = "moments",       // "moments" | "calendar"
    val myCalendarViewMode: String = "dot",      // "dot" | "chip"
    val othersCalendarViewMode: String = "dot",  // "dot" | "chip"
    val defaultEventVisibility: String = "public", // "public" | "friend" | "private"
)
