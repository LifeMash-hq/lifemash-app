package org.bmsk.lifemash.moment.impl

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.bmsk.lifemash.moment.api.PostMomentRoute

fun NavGraphBuilder.postMomentNavGraph(
    onSuccess: () -> Unit,
    onClose: () -> Unit,
) {
    composable<PostMomentRoute> {
        PostMomentRouteScreen(
            onSuccess = onSuccess,
            onClose = onClose,
        )
    }
}
