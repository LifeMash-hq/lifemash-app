package org.bmsk.lifemash.data.network.service

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.functions
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.runTest
import org.bmsk.lifemash.data.network.response.SearchApiResponse
import org.bmsk.lifemash.data.network.response.SearchRequestBody
import org.bmsk.lifemash.data.network.response.SearchResultBody
import org.junit.Rule
import org.junit.Test
import kotlin.time.Duration.Companion.milliseconds

class LifeMashFirebaseServiceTest {

    @get:Rule
    val firebase = FirebaseInitRule()

    @Test
    fun `서비스테스트`(): Unit = runTest {
        val fakeSearchService = object : SearchService {
            override suspend fun search(body: SearchRequestBody) = SearchApiResponse(
                result = SearchResultBody(),
            )
        }
        val lifeMashFirebaseService = LifeMashFirebaseServiceImpl(
            FirebaseFirestore.getInstance(),
            fakeSearchService,
        )

        lifeMashFirebaseService.getArticles(
            category = "international",
        ).also {
            print(it)
        }
    }
}