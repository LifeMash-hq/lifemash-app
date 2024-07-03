package org.bmsk.lifemash.feature.scrap.navigation

import androidx.navigation.NavController
import org.bmsk.lifemash.feature.scrap.ScrapRoute
import org.bmsk.lifemash.feature.scrap.api.ScrapNavController
import javax.inject.Inject

internal class ScrapNavControllerImpl @Inject constructor() : ScrapNavController {
    override fun route(): String = ScrapRoute.ROUTE

    override fun navigate(navController: NavController, navInfo: Unit) {
        navController.navigate(ScrapRoute.ROUTE)
    }
}
