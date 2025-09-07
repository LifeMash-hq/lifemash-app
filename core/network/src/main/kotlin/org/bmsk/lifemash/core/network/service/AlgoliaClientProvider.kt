package org.bmsk.lifemash.core.network.service

import com.algolia.client.api.SearchClient
import com.algolia.client.configuration.ClientOptions
import com.google.firebase.Firebase
import com.google.firebase.functions.functions
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await
import kotlin.time.Duration.Companion.seconds

internal data class AlgoliaClientInfo(
    val client: SearchClient,
    val indexName: String,
)

internal interface AlgoliaClientProvider {
    suspend fun getClientInfo(forceRefresh: Boolean): AlgoliaClientInfo
}

internal class FirebaseAlgoliaClientProvider(
    private val region: String = "asia-northeast3"
) : AlgoliaClientProvider {

    @Volatile
    private var clientInfo: AlgoliaClientInfo? = null

    @Volatile
    private var validUntil: Long? = null // 참고용(로그/디버그)
    private val refreshMutex = Mutex()

    override suspend fun getClientInfo(forceRefresh: Boolean): AlgoliaClientInfo {
        val currentClientInfo = clientInfo
        if (!forceRefresh && currentClientInfo != null) return currentClientInfo

        return refreshMutex.withLock {
            val currentClientInfoInLock = clientInfo
            if (!forceRefresh && currentClientInfoInLock != null) return@withLock currentClientInfoInLock

            val functions = Firebase.functions(region)
            val callable = functions.getHttpsCallable("getAlgoliaSearchKey")

            @Suppress("UNCHECKED_CAST")
            val data = callable.call(null).await().data as Map<String, Any?>

            val appId = data["appId"] as String
            val newIndex = data["indexName"] as String
            val securedApiKey = data["securedApiKey"] as String
            val newValidUntil = (data["validUntil"] as? Number)?.toLong()

            val newClient = SearchClient(
                appId = appId,
                apiKey = securedApiKey,
                options = ClientOptions(
                    connectTimeout = 5.seconds,
                    readTimeout = 8.seconds,
                    writeTimeout = 8.seconds
                )
            )

            val newClientInfo = AlgoliaClientInfo(
                client = newClient,
                indexName = newIndex
            )
            clientInfo = newClientInfo
            validUntil = newValidUntil
            newClientInfo
        }
    }
}
