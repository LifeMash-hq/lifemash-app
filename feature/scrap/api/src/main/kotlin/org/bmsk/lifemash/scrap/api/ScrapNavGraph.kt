package org.bmsk.lifemash.scrap.api

import org.bmsk.lifemash.feature.shared.navigation.LifeMashNavGraph

interface ScrapNavGraph : LifeMashNavGraph<ScrapNavGraphInfo>

data class ScrapNavGraphInfo(
    val onClickNews: (url: String) -> Unit,
    val onShowErrorSnackbar: (Throwable?) -> Unit,
)