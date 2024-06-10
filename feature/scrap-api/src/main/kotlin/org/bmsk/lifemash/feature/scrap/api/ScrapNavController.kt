package org.bmsk.lifemash.feature.scrap.api

import androidx.navigation.NavOptions
import org.bmsk.lifemash.feature.nav.LifeMashNavController

interface ScrapNavController : LifeMashNavController<ScrapNavControllerInfo>

data class ScrapNavControllerInfo(
    val navOptions: NavOptions,
)