package org.bmsk.lifemash.assistant.api

data class AssistantNavGraphInfo(
    val onShowErrorSnackbar: (Throwable?) -> Unit,
    val onBack: () -> Unit,
)
