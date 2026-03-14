package org.bmsk.lifemash.core.network.service

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.functions
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.runTest
import org.bmsk.lifemash.core.network.response.LifeMashArticleCategory
import org.junit.Rule
import org.junit.Test
import kotlin.time.Duration.Companion.milliseconds

class LifeMashFirebaseServiceTest {

    @get:Rule
    val firebase = FirebaseInitRule()

    @Test
    fun `서비스테스트`(): Unit = runTest {
        val lifeMashFirebaseService = LifeMashFirebaseServiceImpl(
            FirebaseFirestore.getInstance()
        )

        lifeMashFirebaseService.getArticles(
            category = LifeMashArticleCategory.INTERNATIONAL,
        ).also {
            print(it)
        }
    }
}