package org.bmsk.lifemash.history.api

data class HistoryNavGraphInfo(
    val onClickArticle: (url: String) -> Unit,
    val onShowErrorSnackbar: (Throwable?) -> Unit,
)
