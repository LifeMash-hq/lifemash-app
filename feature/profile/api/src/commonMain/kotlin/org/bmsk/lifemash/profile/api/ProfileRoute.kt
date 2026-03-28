package org.bmsk.lifemash.profile.api

import kotlinx.serialization.Serializable

@Serializable
data object ProfileRoute

@Serializable
data class UserProfileRoute(val userId: String)

@Serializable
data object PostMomentRoute

const val PROFILE_ROUTE = "profile"

data class ProfileNavGraphInfo(
    val onShowErrorSnackbar: (Throwable?) -> Unit,
    val onNavigateToEventDetail: (String) -> Unit,
    val onNavigateToUserProfile: (String) -> Unit,
)
