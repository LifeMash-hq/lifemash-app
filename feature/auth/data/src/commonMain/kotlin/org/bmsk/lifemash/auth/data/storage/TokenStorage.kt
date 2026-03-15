package org.bmsk.lifemash.auth.data.storage

import org.bmsk.lifemash.auth.domain.model.AuthToken

// TODO: Phase 2 - DataStore/Settings KMP로 영속화
internal class TokenStorage {
    private var token: AuthToken? = null

    fun get(): AuthToken? = token
    fun save(authToken: AuthToken) { token = authToken }
    fun clear() { token = null }
}
