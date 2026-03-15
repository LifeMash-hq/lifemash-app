package org.bmsk.lifemash.feed.data.subscription.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface CategorySubscriptionDataSource {
    fun getSubscribedCategoryKeys(): Flow<Set<String>>
    suspend fun setSubscribedCategoryKeys(keys: Set<String>)
}

class CategorySubscriptionDataSourceImpl(
    private val dataStore: DataStore<Preferences>,
) : CategorySubscriptionDataSource {
    private val key = stringSetPreferencesKey("subscribed_categories")

    override fun getSubscribedCategoryKeys(): Flow<Set<String>> =
        dataStore.data.map { prefs -> prefs[key] ?: emptySet() }

    override suspend fun setSubscribedCategoryKeys(keys: Set<String>) {
        dataStore.edit { prefs -> prefs[key] = keys }
    }
}
