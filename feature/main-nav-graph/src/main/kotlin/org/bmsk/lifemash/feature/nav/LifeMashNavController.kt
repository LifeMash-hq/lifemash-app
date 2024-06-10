package org.bmsk.lifemash.feature.nav

import androidx.navigation.NavController

interface LifeMashNavController<T> {
    fun route(): String = ""
    fun navigate(navController: NavController, navInfo: T)
}