package org.bmsk.lifemash.auth.data.storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.bmsk.lifemash.auth.domain.model.AuthToken

class TokenStorage(private val dataStore: DataStore<Preferences>) {

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

    private companion object {
        val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
    }
}
