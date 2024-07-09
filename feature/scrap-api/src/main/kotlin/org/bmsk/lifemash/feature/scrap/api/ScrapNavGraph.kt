package org.bmsk.lifemash.feature.scrap.api

import org.bmsk.lifemash.feature.nav.LifeMashNavGraph

interface ScrapNavGraph : LifeMashNavGraph<ScrapNavGraphInfo>

data class ScrapNavGraphInfo(
    val onClickNews: (url: String) -> Unit,
    val onShowErrorSnackbar: (Throwable?) -> Unit,
)