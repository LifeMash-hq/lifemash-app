package org.bmsk.lifemash.feature.scrap.api

import org.bmsk.lifemash.feature.nav.LifeMashNavGraph

interface ScrapNavGraph : LifeMashNavGraph<ScrapNavGraphInfo>

data class ScrapNavGraphInfo(
    val onShowErrorSnackbar: (Throwable?) -> Unit,
)