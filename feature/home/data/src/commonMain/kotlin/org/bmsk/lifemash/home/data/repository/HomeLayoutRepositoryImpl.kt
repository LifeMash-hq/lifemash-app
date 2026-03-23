package org.bmsk.lifemash.home.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bmsk.lifemash.home.api.HomeBlock
import org.bmsk.lifemash.home.domain.repository.HomeLayoutRepository

private val LAYOUT_KEY = stringPreferencesKey("home_block_layout")

@Serializable
private data class HomeBlockJson(
    val id: String,
    val visible: Boolean,
    val url: String? = null,
)

private val defaultLayout = listOf(
    HomeBlockJson(id = "CALENDAR_TODAY", visible = true),
    HomeBlockJson(id = "GROUPS", visible = true),
    HomeBlockJson(id = "ASSISTANT", visible = true),
)

private fun HomeBlockJson.toDomain(): HomeBlock = when (id) {
    "CALENDAR_TODAY" -> HomeBlock.CalendarToday(visible = visible)
    "GROUPS" -> HomeBlock.Groups(visible = visible)
    "ASSISTANT" -> HomeBlock.Assistant(visible = visible)
    else -> HomeBlock.WebViewBlock(blockId = id, url = url.orEmpty(), visible = visible)
}

private fun HomeBlock.toJson(): HomeBlockJson = when (this) {
    is HomeBlock.CalendarToday -> HomeBlockJson(id = id, visible = visible)
    is HomeBlock.Groups -> HomeBlockJson(id = id, visible = visible)
    is HomeBlock.Assistant -> HomeBlockJson(id = id, visible = visible)
    is HomeBlock.WebViewBlock -> HomeBlockJson(id = id, visible = visible, url = url)
}

class HomeLayoutRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
) : HomeLayoutRepository {

    override fun getLayout(): Flow<List<HomeBlock>> =
        dataStore.data.map { prefs ->
            val json = prefs[LAYOUT_KEY]
            if (json.isNullOrBlank()) {
                defaultLayout.map { it.toDomain() }
            } else {
                runCatching {
                    Json.decodeFromString<List<HomeBlockJson>>(json)
                        .map { it.toDomain() }
                }.getOrElse { defaultLayout.map { it.toDomain() } }
            }
        }

    override suspend fun saveLayout(blocks: List<HomeBlock>) {
        val json = Json.encodeToString(blocks.map { it.toJson() })
        dataStore.edit { prefs -> prefs[LAYOUT_KEY] = json }
    }
}
