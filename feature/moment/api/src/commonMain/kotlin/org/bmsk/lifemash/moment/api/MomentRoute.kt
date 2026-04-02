package org.bmsk.lifemash.moment.api

import kotlinx.serialization.Serializable

@Serializable
data object PostMomentRoute

@Serializable
data class UserMomentsRoute(val userId: String)
