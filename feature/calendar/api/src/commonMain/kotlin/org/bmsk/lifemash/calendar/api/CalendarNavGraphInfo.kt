package org.bmsk.lifemash.calendar.api

data class CalendarNavGraphInfo(
    val onShowErrorSnackbar: (Throwable?) -> Unit,
    val onBack: () -> Unit = {},
    val onNavigateToEventCreate: (year: Int, month: Int, day: Int, groupId: String?) -> Unit = { _, _, _, _ -> },
)
