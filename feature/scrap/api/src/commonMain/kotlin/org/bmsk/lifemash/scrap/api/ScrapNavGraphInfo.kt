package org.bmsk.lifemash.scrap.api

data class ScrapNavGraphInfo(
    val onClickNews: (url: String) -> Unit,
    val onShowErrorSnackbar: (Throwable?) -> Unit,
)
