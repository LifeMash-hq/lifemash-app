package org.bmsk.lifemash.scrap.ui.navigation

import androidx.navigation.NavController
import org.bmsk.lifemash.scrap.ui.ScrapRoute
import org.bmsk.lifemash.scrap.api.ScrapNavController
import javax.inject.Inject

internal class ScrapNavControllerImpl @Inject constructor() : ScrapNavController {
    override fun route(): String = ScrapRoute.ROUTE

    override fun navigate(navController: NavController, navInfo: Unit) {
        navController.navigate(ScrapRoute.ROUTE)
    }
}
