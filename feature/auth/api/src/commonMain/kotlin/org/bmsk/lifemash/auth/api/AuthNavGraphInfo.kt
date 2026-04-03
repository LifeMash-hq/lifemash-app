package org.bmsk.lifemash.auth.api

data class AuthNavGraphInfo(
    val onSignInComplete: (isNewUser: Boolean) -> Unit,
    val onShowErrorSnackbar: (Throwable?) -> Unit,
)
