package org.bmsk.lifemash.domain.session

import kotlinx.coroutines.flow.Flow

interface SessionProvider {
    fun getCurrentUserId(): Flow<String?>
    suspend fun signOut()
}
