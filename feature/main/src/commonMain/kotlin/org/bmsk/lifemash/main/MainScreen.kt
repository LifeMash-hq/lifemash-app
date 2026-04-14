package org.bmsk.lifemash.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.bmsk.lifemash.auth.api.AuthNavGraphInfo
import org.bmsk.lifemash.auth.api.AuthRoute
import org.bmsk.lifemash.auth.impl.authNavGraph
import org.bmsk.lifemash.calendar.api.CalendarNavGraphInfo
import org.bmsk.lifemash.calendar.impl.calendarNavGraph
import org.bmsk.lifemash.eventdetail.api.EventDetailRoute
import org.bmsk.lifemash.eventdetail.impl.eventDetailNavGraph
import org.bmsk.lifemash.memo.api.MemoNavGraphInfo
import org.bmsk.lifemash.memo.impl.memoNavGraph
import org.bmsk.lifemash.onboarding.api.OnboardingNavGraphInfo
import org.bmsk.lifemash.onboarding.api.OnboardingRoute
import org.bmsk.lifemash.onboarding.impl.onboardingNavGraph
import org.bmsk.lifemash.profile.api.ProfileEditRoute
import org.bmsk.lifemash.profile.api.UserProfileRoute
import org.bmsk.lifemash.profile.impl.profileEditNavGraph
import org.bmsk.lifemash.profile.impl.userProfileNavGraph
import org.koin.compose.viewmodel.koinViewModel
import org.bmsk.lifemash.calendar.api.EventCreateRoute

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    onShowErrorSnackbar: (Throwable?) -> Unit = {}
) {
    val rootNavController = rememberNavController()
    val mainViewModel = koinViewModel<MainViewModel>()
    val authState by mainViewModel.authState.collectAsStateWithLifecycle()

    println("[AuthDiag] MainScreen recompose, authState=$authState")

    if (authState is AuthState.Loading) return

    val currentUser = when (val state = authState) {
        is AuthState.Authenticated -> state.user
        is AuthState.Unauthenticated, AuthState.Loading -> null
    }

    val startDestination: Any = remember { if (currentUser != null) MainTabRoute else AuthRoute }
    println("[AuthDiag] MainScreen startDest=$startDestination, currentUser=${currentUser?.nickname}, backStack=${rootNavController.currentDestination?.route}")

    NavHost(
        navController = rootNavController,
        startDestination = startDestination,
        modifier = modifier.fillMaxSize(),
    ) {
        // 탭 화면 (바텀바는 MainTabScreen 내부에서 항상 표시)
        composable<MainTabRoute> {
            MainTabScreen(
                currentUser = currentUser,
                onShowErrorSnackbar = onShowErrorSnackbar,
                onNavigateToEventDetail = { eventId ->
                    rootNavController.navigate(EventDetailRoute(eventId))
                },
                onNavigateToProfileEdit = {
                    rootNavController.navigate(ProfileEditRoute)
                },
                onNavigateToEventCreate = { year, month, day ->
                    rootNavController.navigate(
                        EventCreateRoute(year, month, day)
                    )
                },
                onNavigateToUserProfile = { userId ->
                    rootNavController.navigate(UserProfileRoute(userId))
                },
                onNavigateToAuth = {
                    rootNavController.navigate(AuthRoute)
                },
            )
        }

        // 전체화면 routes (바텀바 없음)
        eventDetailNavGraph(onBack = { rootNavController.popBackStack() })

        calendarNavGraph(
            navInfo = CalendarNavGraphInfo(
                onShowErrorSnackbar = onShowErrorSnackbar,
                onBack = { rootNavController.popBackStack() },
            ),
            navController = rootNavController,
        )

        memoNavGraph(
            MemoNavGraphInfo(
                onShowErrorSnackbar = onShowErrorSnackbar,
                onBack = { rootNavController.popBackStack() },
            )
        )

        profileEditNavGraph(onBack = { rootNavController.popBackStack() })

        userProfileNavGraph(
            onBack = { rootNavController.popBackStack() },
            onNavigateToEventDetail = { eventId ->
                rootNavController.navigate(EventDetailRoute(eventId))
            },
        )

        authNavGraph(
            AuthNavGraphInfo(
                onSignInComplete = { isNewUser ->
                    val destination = if (isNewUser) OnboardingRoute else MainTabRoute
                    println("[AuthDiag] onSignInComplete navigate to $destination, current=${rootNavController.currentDestination?.route}")
                    rootNavController.navigate(destination) {
                        popUpTo(rootNavController.graph.startDestinationId) { inclusive = true }
                        launchSingleTop = true
                    }
                    println("[AuthDiag] onSignInComplete navigate done")
                },
                onShowErrorSnackbar = onShowErrorSnackbar,
            )
        )

        onboardingNavGraph(
            OnboardingNavGraphInfo(
                onOnboardingComplete = {
                    rootNavController.navigate(MainTabRoute) {
                        popUpTo(rootNavController.graph.startDestinationId) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onShowErrorSnackbar = onShowErrorSnackbar,
            )
        )
    }
}