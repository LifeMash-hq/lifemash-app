package org.bmsk.lifemash.notification.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import kotlinx.collections.immutable.persistentListOf
import kotlin.time.Clock
import org.bmsk.lifemash.notification.domain.model.NotificationKeyword
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class NotificationScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun empty_상태에서_안내_텍스트가_표시된다() {
        composeTestRule.setContent {
            NotificationScreen(
                uiState = NotificationUiState.Empty,
                onAddKeyword = {},
                onRemoveKeyword = {},
                onBack = {},
            )
        }

        composeTestRule.onNodeWithText("등록된 키워드가 없습니다").assertIsDisplayed()
    }

    @Test
    fun loaded_상태에서_키워드_목록이_표시된다() {
        val keywords = persistentListOf(
            NotificationKeyword(id = 1, keyword = "삼성 반도체", createdAt = Clock.System.now()),
            NotificationKeyword(id = 2, keyword = "AI 규제", createdAt = Clock.System.now()),
        )
        composeTestRule.setContent {
            NotificationScreen(
                uiState = NotificationUiState.Loaded(keywords),
                onAddKeyword = {},
                onRemoveKeyword = {},
                onBack = {},
            )
        }

        composeTestRule.onNodeWithText("삼성 반도체").assertIsDisplayed()
        composeTestRule.onNodeWithText("AI 규제").assertIsDisplayed()
    }

    @Test
    fun 키워드_입력_후_추가_버튼_클릭_시_onAddKeyword가_호출된다() {
        var addedKeyword: String? = null
        composeTestRule.setContent {
            NotificationScreen(
                uiState = NotificationUiState.Empty,
                onAddKeyword = { addedKeyword = it },
                onRemoveKeyword = {},
                onBack = {},
            )
        }

        composeTestRule.onNodeWithTag("keyword_input").performTextInput("삼성")
        composeTestRule.onNodeWithText("추가").performClick()

        assertEquals("삼성", addedKeyword)
    }

    @Test
    fun 삭제_버튼_클릭_시_onRemoveKeyword가_호출된다() {
        var removedId: Long? = null
        val keywords = persistentListOf(
            NotificationKeyword(id = 1, keyword = "삼성", createdAt = Clock.System.now()),
        )
        composeTestRule.setContent {
            NotificationScreen(
                uiState = NotificationUiState.Loaded(keywords),
                onAddKeyword = {},
                onRemoveKeyword = { removedId = it },
                onBack = {},
            )
        }

        composeTestRule.onNodeWithContentDescription("삭제").performClick()

        assertEquals(1L, removedId)
    }

    @Test
    fun 뒤로가기_버튼_클릭_시_onBack이_호출된다() {
        var backCalled = false
        composeTestRule.setContent {
            NotificationScreen(
                uiState = NotificationUiState.Empty,
                onAddKeyword = {},
                onRemoveKeyword = {},
                onBack = { backCalled = true },
            )
        }

        composeTestRule.onNodeWithContentDescription("뒤로").performClick()

        assertTrue(backCalled)
    }
}
