package org.bmsk.lifemash.memo.impl

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.bmsk.lifemash.memo.api.MemoNavGraphInfo
import org.bmsk.lifemash.memo.api.MemoRoute

fun NavGraphBuilder.memoNavGraph(navInfo: MemoNavGraphInfo) {
    composable<MemoRoute> {
        MemoRouteScreen(
            onShowErrorSnackbar = navInfo.onShowErrorSnackbar,
            onBack = navInfo.onBack,
        )
    }
}
