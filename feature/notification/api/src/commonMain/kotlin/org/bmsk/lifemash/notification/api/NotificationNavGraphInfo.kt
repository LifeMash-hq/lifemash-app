package org.bmsk.lifemash.notification.api

data class NotificationNavGraphInfo(
    val onShowErrorSnackbar: (Throwable?) -> Unit,
    val onBack: () -> Unit,
)
