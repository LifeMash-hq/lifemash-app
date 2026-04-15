@file:OptIn(kotlin.time.ExperimentalTime::class)

package org.bmsk.lifemash.notification.impl

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import kotlinx.collections.immutable.persistentListOf
import kotlin.time.Clock
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
                onRetry = {},
                onNotificationClick = {},
            )
        }

        composeTestRule.onNodeWithText("새로운 알림이 없어요").assertIsDisplayed()
    }

    @Test
    fun loaded_상태에서_알림_목록이_표시된다() {
        val notifications = persistentListOf(
            NotificationUi.Comment(
                id = "1",
                isUnread = true,
                createdAt = Clock.System.now(),
                targetId = null,
                actorName = "이수아",
                quote = "축하해!",
            ),
            NotificationUi.Follow(
                id = "2",
                isUnread = false,
                createdAt = Clock.System.now(),
                targetId = null,
                actorName = "정재원",
            ),
        )
        composeTestRule.setContent {
            NotificationScreen(
                uiState = NotificationUiState.Loaded(notifications),
                onRetry = {},
                onNotificationClick = {},
            )
        }

        composeTestRule.onNodeWithText("이수아", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("정재원", substring = true).assertIsDisplayed()
    }

    @Test
    fun error_상태에서_다시_시도_버튼이_표시된다() {
        composeTestRule.setContent {
            NotificationScreen(
                uiState = NotificationUiState.Error("오류 발생"),
                onRetry = {},
                onNotificationClick = {},
            )
        }

        composeTestRule.onNodeWithText("다시 시도").assertIsDisplayed()
    }
}
