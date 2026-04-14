package org.bmsk.lifemash.auth.impl

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.bmsk.lifemash.auth.api.AuthNavGraphInfo
import org.bmsk.lifemash.auth.api.AuthRoute

fun NavGraphBuilder.authNavGraph(navInfo: AuthNavGraphInfo) {
    composable<AuthRoute> {
        AuthRouteScreen(
            onSignInComplete = navInfo.onSignInComplete,
            onShowErrorSnackbar = navInfo.onShowErrorSnackbar,
        )
    }
}
