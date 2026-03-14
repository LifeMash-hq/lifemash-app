package org.bmsk.lifemash.feature.shared.navigation

import androidx.navigation.NavController

interface LifeMashNavController<T> {
    fun route(): String
    fun navigate(navController: NavController, navInfo: T)
}