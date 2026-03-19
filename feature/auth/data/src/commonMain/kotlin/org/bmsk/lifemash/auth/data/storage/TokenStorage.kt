package org.bmsk.lifemash.auth.data.storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import org.bmsk.lifemash.auth.domain.model.AuthToken
import org.bmsk.lifemash.auth.domain.model.AuthUser

class TokenStorage(private val dataStore: DataStore<Preferences>) {

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun get(): AuthToken? {
        val prefs = dataStore.data.first()
        val access = prefs[ACCESS_TOKEN_KEY] ?: return null
        val refresh = prefs[REFRESH_TOKEN_KEY] ?: return null
        return AuthToken(accessToken = access, refreshToken = refresh)
    }

    suspend fun save(authToken: AuthToken) {
        dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN_KEY] = authToken.accessToken
            prefs[REFRESH_TOKEN_KEY] = authToken.refreshToken
        }
    }

    suspend fun clear() {
        dataStore.edit { prefs ->
            prefs.remove(ACCESS_TOKEN_KEY)
            prefs.remove(REFRESH_TOKEN_KEY)
        }
    }

    fun userFlow(): Flow<AuthUser?> = dataStore.data.map { prefs ->
        prefs[USER_KEY]?.let { json.decodeFromString<AuthUser>(it) }
    }

    suspend fun saveUser(user: AuthUser) {
        dataStore.edit { prefs ->
            prefs[USER_KEY] = json.encodeToString(AuthUser.serializer(), user)
        }
    }

    suspend fun clearUser() {
        dataStore.edit { prefs ->
            prefs.remove(USER_KEY)
        }
    }

    private companion object {
        val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
        val USER_KEY = stringPreferencesKey("user")
    }
}
