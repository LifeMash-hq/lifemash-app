package org.bmsk.lifemash.auth.api

data class AuthNavGraphInfo(
    val onSignInComplete: () -> Unit,
    val onShowErrorSnackbar: (Throwable?) -> Unit,
)
