package org.bmsk.lifemash.memo.api

data class MemoNavGraphInfo(
    val onShowErrorSnackbar: (Throwable?) -> Unit,
    val onBack: () -> Unit = {},
)
