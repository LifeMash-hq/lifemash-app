package org.bmsk.lifemash.core.network.service

import com.algolia.client.api.SearchClient
import com.algolia.client.exception.AlgoliaApiException
import com.algolia.client.exception.AlgoliaRetryException
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class LifeMashAlgoliaServiceTest {
    @get:Rule
    val firebase = FirebaseInitRule()

    @Test
    fun `검색_개발_점검용`() = runTest {
        val lifeMashAlgoliaService = LifeMashAlgoliaServiceImpl()
        val searchResult = lifeMashAlgoliaService.search(
            query = "미국",
        )

        searchResult.forEachIndexed { index, hit ->
            println("Index: $index, Hit: $hit")
        }
    }

    @Test
    fun `검색_유효하지_않은_appId_및_apiKey_사용`() {
        val fakeAlgoliaClientProvider = FakeAlgoliaClientProvider(
            fakeClient = SearchClient(
                appId = "invalid_app_id",
                apiKey = "invalid_key"
            ),
            indexName = "index_name"
        )
        val lifeMashAlgoliaService = LifeMashAlgoliaServiceImpl(
            algoliaClientProvider = fakeAlgoliaClientProvider
        )

        assertThrows(AlgoliaRetryException::class.java) {
            runTest {
                lifeMashAlgoliaService.search(query = "query")
            }
        }
    }

    @Test
    fun `검색_유효하지않은_apiKey만_사용하는_경우`() = runTest {
        val algoliaClientProvider = FirebaseAlgoliaClientProvider()
        val fakeAlgoliaClientProvider = FakeAlgoliaClientProvider(
            fakeClient = SearchClient(
                appId = algoliaClientProvider.getClientInfo(forceRefresh = false).client.appId,
                apiKey = "invalid_key"
            ),
            indexName = "index_name"
        )
        val lifeMashAlgoliaService = LifeMashAlgoliaServiceImpl(
            algoliaClientProvider = fakeAlgoliaClientProvider
        )

        val result = runCatching {
            lifeMashAlgoliaService.search(query = "query")
        }

        assertTrue(result.exceptionOrNull() is AlgoliaApiException)
    }
}

private class FakeAlgoliaClientProvider(
    private val fakeClient: SearchClient,
    private val indexName: String,
) : AlgoliaClientProvider {
    override suspend fun getClientInfo(forceRefresh: Boolean): AlgoliaClientInfo {
        return AlgoliaClientInfo(
            client = fakeClient,
            indexName = indexName
        )
    }
}